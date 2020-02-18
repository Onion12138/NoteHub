"""

@file: extract_title_content_keywords.py

@time: created at 2020/02/13, modified at 2.18

@desc: 1. 嵌套的对象转dict
       2. 将dict转json
       3. 提取txt文件（md原文）的'#'类型title并转换为对象

"""

import os
import re
import json
import jieba
from jieba.analyse import *
from textrank4zh import TextRank4Sentence

class Menu:
    level = int
    children = list
    value = str

    def __init__(self, level, value):
        self.level = level
        self.children = []
        self.value = value

    def set_parent(self, p):
        p.add_children(self)

    def add_children(self, children):
        self.children.append(children)

    def compareTo(self, other):
        return -(self.value - other.value)

    # def to_json_str(self):
    # dict = self.__dict__
    # c_list = []
    # for c in self.children:
    #     c_list.append(c.__dict__)
    # dict["children"] = c_list
    # return str(dict)


def to_json_str(obj):
    dict = obj.__dict__
    c_list = []
    for c in obj.children:
        c_list.append(to_json_str(c))
    dict["children"] = c_list
    return dict


def construct_title(input):
    head = Menu(0, "")
    content = ""
    last = {0: head}
    found = 0
    titles = ""
    for line in input.split("\n"):
        line = line.strip()
        if line is '':
            continue
        if line.startswith("```") and found is 0:
            found = 1
            continue
        elif line.startswith("```"):
            found = 0
            continue
        if found is 0:
            reg = "[^A-Za-z\u4e00-\u9fa5]"
            content += re.sub(reg, "", line) + '\n'
            if len(re.findall(r"^#+ (.*)", line)):
                # print(line)
                length = len(line.split(" ")[0])
                t = Menu(length, line[length+1:])
                titles += line[length+1:] + ","
                last[length] = t
                ll = length - 1
                while last.get(ll) is None:
                    ll -= 1
                last[ll].add_children(t)
                continue

    return head, content, titles[:len(titles)-1].strip()

def do_extract_summarize(content):
    tr4s = TextRank4Sentence()
    tr4s.analyze(text=content, lower=True, source='no_stop_words')
    key_sentences = tr4s.get_key_sentences(num=1, sentence_min_len=2)
    return key_sentences.pop(0)['sentence']


def do_extract(input):
    head, content, titles = construct_title(input)
    # level_titles = json.dumps(to_json_str(head), ensure_ascii=False)
    return to_json_str(head), titles, content, do_extract_keywords(content), do_extract_summarize(content)


def do_extract_keywords(content):
    tags = extract_tags(content, topK=20, withWeight=True)
    rank = textrank(content, topK=20, withWeight=True)
    # 如果产生越界问题，抛出异常
    try:
        max_tag = tags[0][1]
        min_tag = tags[-1][1]
        max_rank = rank[0][1]
        min_rank = rank[-1][1]
        for k, w in tags:
            if max_tag != min_tag:
                keyword[k] = (w - min_tag) / (max_tag - min_tag)
            else:
                keyword[k] = 0

        for k, w in rank:
            if max_rank != min_rank:
                if k in keyword.keys():
                    keyword[k] += (w - min_rank) / (max_rank - min_rank)
                else:
                    keyword[k] = (w - min_rank) / (max_rank - min_rank)
            else:
                if k in keyword.keys():
                    keyword[k] += 0
                else:
                    keyword[k] = 0
    except:
        sorted_keywords = tags
    else:
        sorted_keywords = sorted(keyword.items(), key=lambda x: -x[1])[:10]
    finally:
        final_keywords = ""
        for item in sorted_keywords:
            final_keywords += item[0] + ","
        final_keywords = final_keywords[:len(final_keywords) - 1]
        return final_keywords

if __name__ == '__main__':
    f = open("test.txt")
    input = f.read()
    level_titles, titles, content, keywords, summary = do_extract(input)
    print(level_titles)
    print(titles)
    print(keywords)
    print(summary)
    # print( to_json_str(construct_title(level_titles)))
    # print(json.dumps(to_json_str(construct_title(level_titles)), ensure_ascii=False))
