import os
import numpy as np
import operator
import pymongo
from py2neo import Graph,Node,Relationship

def get_note_info(input_file):
    """
    :param input_file: the input file should include id, title and tag, which are split by comma.
    :return:a dict: key = note_id, value = [title, tag]
    """
    note_info = {}
    if not os.path.exists(input_file):
        return {}
    fp = open(input_file)
    for line in fp:
        note = line.strip().split(',')
        # invalid line will be omitted
        if len(note) < 3:
            continue
        # if comma is allowed to be in the title, there will be some problems.
        else:
            note_id = note[0]
            note_title = note[1]
            note_tag = ",".join(note[2:])
        note_info[note_id] = [note_title, note_tag]
    fp.close()
    return note_info


def get_score(star, hate, collect, view):
    """
    hate for -1
    no view for 0
    star and collect counts 1
    view counts 0.01
    :return:the final score of a note
    """
    score = 0
    if hate == 1:
        return -1
    else:
        score = float(star + collect + view / 100)
        return score


def get_avr_score(input_file):
    """
    to get the average score of the note based on star, hate, collect, view.
    And the score will be used to get the negative sample note later.
    :param input_file:user_id, note_id, star, hate, collect, view
    (the next version will make use of the graph database, which is coming soon)
    :return:a dict: key = note_id, value = average score
    """
    avr_score_dict = {}
    if not os.path.exists(input_file):
        return {}
    fp = open(input_file)
    for line in fp:
        behave = line.strip().split(',')
        # filter the invalid line
        if len(behave) < 6:
            continue
        else:
            user_id, note_id, star, hate, collect, view = \
                behave[0], behave[1], behave[2], behave[3], behave[4], behave[5]
            if note_id not in avr_score_dict:
                avr_score_dict[note_id] = [0, 0]
            avr_score_dict[note_id][0] += 1
            avr_score_dict[note_id][1] += get_score(int(star), int(hate), int(collect), int(view))
    fp.close()
    for note_id in avr_score_dict:
        avr_score_dict[note_id] = round(avr_score_dict[note_id][1] / avr_score_dict[note_id][0], 3)
    return avr_score_dict


def get_train_data(input_file):
    """
    to get the train data for the LFM algorithm
    :param input_file:user_id, note_id, star（0，1）, hate（0，1）, collect（0，1）, view
    :return:a list whose element is tuple
    """
    if not os.path.exists(input_file):
        return []
    avr_dict = get_avr_score(input_file)
    neg_dict = {}
    pos_dict = {}

    user_dict = {}
    note_list = []

    train_data = []
    fp = open(input_file)
    for line in fp:
        behave = line.strip().split(',')
        # filter the invalid line
        if len(behave) < 6:
            continue
        else:
            user_id, note_id, star, hate, collect, view = \
                behave[0], behave[1], behave[2], behave[3], behave[4], behave[5]
            score = get_score(int(star), int(hate), int(collect), int(view))
            if note_id not in note_list:
                note_list.append(note_id)
            if user_id not in pos_dict:
                pos_dict[user_id] = []
                user_dict[user_id] = []
            if user_id not in neg_dict:
                neg_dict[user_id] = []
            pos_dict[user_id].append((note_id, score))
            user_dict[user_id].append(note_id)
    for key in user_dict:
        diff = set(note_list).difference(set(user_dict[key]))
        for index in diff:
            score = avr_dict.get(index, 0)
            neg_dict[key].append((index, score))
    fp.close()
    #  to get the negative sample
    for user_id in pos_dict:
        data_num = min(len(pos_dict[user_id]), len(neg_dict.get(user_id, [])))
        # print(data_num)
        if data_num > 0:
            train_data += [(user_id, tup[0], tup[1]) for tup in pos_dict[user_id]][:data_num]
        else:
            continue
        sorted_neg_list = sorted(neg_dict[user_id], key=lambda element: element[1], reverse=True)[:data_num]
        train_data += [(user_id, tup[0], 0) for tup in sorted_neg_list]
    return train_data


def init_model(dimension):
    return np.random.randn(dimension)


def model_predict(user_vec, note_vec):
    res = np.dot(user_vec, note_vec) / (np.linalg.norm(user_vec) * np.linalg.norm(note_vec))
    return res


def lfm_train(train_data, dimension, re_fac, lr, step):
    """
    :param train_data:train data
    :param dimension:dimension of the output vector
    :param re_fac:regulation factor
    :param lr:learning rate
    :param step:iteration num
    :return:
        user_vec_dict: key user_id, value list
        note_vec_dict: key note_id, value list
    """
    user_vec_dict = {}
    note_vec_dict = {}
    for step_index in range(step):
        for data_instance in train_data:
            user_id, note_id, score = data_instance
            if user_id not in user_vec_dict:
                user_vec_dict[user_id] = init_model(dimension)
            if note_id not in note_vec_dict:
                note_vec_dict[note_id] = init_model(dimension)
        delta = score - model_predict(user_vec_dict[user_id], note_vec_dict[note_id])
        for index in range(dimension):
            user_vec_dict[user_id][index] += lr * (
                        delta * note_vec_dict[note_id][index] - re_fac * user_vec_dict[user_id][index])
            note_vec_dict[note_id][index] += lr * (
                        delta * user_vec_dict[user_id][index] - re_fac * note_vec_dict[note_id][index])
        lr = lr * 0.9
    return user_vec_dict, note_vec_dict


def get_rec_user_result(user_vec_dict, note_vec_dict, user_id):
    """

    :param user_vec: output of the lfm train
    :param note_vec: output of the lfm train
    :param user_id: user who wants the recommend list
    :return: a list whose element is a tuple (note_id, score)
    """
    if user_id not in user_vec_dict:
        return []
    record = {}
    recommend_list = []
    rec_num = 3
    user_vec = user_vec_dict[user_id]
    for note_id in note_vec_dict:
        note_vec = note_vec_dict[note_id]
        res = model_predict(user_vec, note_vec)
        record[note_id] = res
    for zuhe in sorted(record.items(), key=operator.itemgetter(1), reverse=True)[:rec_num]:
        note_id = zuhe[0]
        score = round(zuhe[1], 3)
        recommend_list.append((note_id, score))
    return recommend_list


def get_rec_note_result(note_vec_dict, note_id):
    """
    :param note_vec_dict:
    :param note_id:
    :return: a list whose element is a tuple (note_id, score)
    """
    if note_id not in note_vec_dict:
        return []
    this_note_vec = note_vec_dict[note_id]
    record = {}
    recommend_list = []
    rec_num = 3
    for note_id in note_vec_dict:
        note_vec = note_vec_dict[note_id]
        print(note_vec)
        print(this_note_vec)
        res = np.sqrt(np.sum(np.square(note_vec - this_note_vec)))
        print(res)
        record[note_id] = res
    for zuhe in sorted(record.items(), key=operator.itemgetter(1), reverse=True)[:rec_num]:
        note_id = zuhe[0]
        score = round(zuhe[1], 3)
        recommend_list.append((note_id, score))
    return recommend_list

def update_file():
    # client = pymongo.MongoClient(host='localhost', port=27017)
    graph = Graph("http://localhost:7474", username="neo4j", password="neo4j")
    # db = client.notehub
    # notes = db.note
    data = []
    # for note in notes.find({}, {"id": 1, "authorEmail": 1, "alexa": 1}):
    #     data.append(note)

def do_rec():
    train_data = get_train_data("behave.txt")
    user_vec_dict, note_vec_dict = lfm_train(train_data, 2, 0.01, 0.1, 500)





if __name__ == '__main__':
    train_data = get_train_data("behave.txt")
    user_vec_dict, note_vec_dict = lfm_train(train_data, 2, 0.01, 0.1, 500)
    print(get_rec_user_result(user_vec_dict, note_vec_dict, '1'))
    print(get_rec_note_result(note_vec_dict, '1'))

    # avr_dict = get_avr_score("behave.txt")
    # print(avr_dict)
    #  note_dict = get_note_info("note.txt")
    #  print(note_dict)
