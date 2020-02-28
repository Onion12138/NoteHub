from tensorflow.keras.preprocessing.text import Tokenizer
from tensorflow.keras.preprocessing.sequence import pad_sequences
from tensorflow.keras.models import load_model
import extract_title_content_keywords
import numpy as np
import pickle
import jieba
import os
import chardet
import re

# os.environ["KMP_DUPLICATE_LIB_OK"] = "TRUE"  # MacOs

pattern = re.compile('[^\u4e00-\u9fa5]')  # 再去一次
max_sequence_length = 150

def pickle_load(file_path):
    return pickle.load(open(file_path, 'rb'))


def pickle_dump(obj, file_path):
    pickle.dump(obj, open(file_path, 'wb'))


def get_in_one_line(input):
    data = []
    chinese_content = []
    for line in input.split("\n"):
        line = pattern.sub("", line)
        line_cut = jieba.cut(line)
        chinese_content.extend(list(line_cut))
    if len(chinese_content) <= 0:
        raise Exception("无内容，five！")
    else:
        sequence = " ".join(chinese_content)
        data.append(sequence)
    return data

def serialization(data, tokenizer):
    x = tokenizer.texts_to_sequences(data)
    x = pad_sequences(x, max_sequence_length)
    return x

def do_predict(input):
    it = serialization(get_in_one_line(input), pickle_load("./tokenizer"))
    model = load_model("model_2020_1.h5")
    res = model.predict(np.array(it))[0][0]
    # print(res)
    if res >= 0.6:
        return False
    return True


def predict(content):
    a = True
    try:
        a = do_predict(content)
    except:
        print("some thing wrong here.")
    finally:
        return a


if __name__ == '__main__':
    f = open("test.txt")
    note = f.read()
    level_titles, titles, content, keywords, summary = extract_title_content_keywords.do_extract(note)
    do_predict(content)