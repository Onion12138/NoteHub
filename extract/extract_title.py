"""

@file: extract_title.py

@time: 2020/02/13

@desc: 1. 嵌套的对象转dict
       2. 将dict转json
       3. 提取txt文件（md原文）的'#'类型title并转换为对象

"""

import os
import re
import json

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



def preprocess(raw_str):
    text = raw_str.split("\n")
    record = True
    level_titles = []
    languages = set()
    final = ""
    for line in text:
        if line.strip() == '':
            continue
        if len(re.findall(r"#{1,4} (.*)", line)):
            level_titles.append(line)
            continue
        if line.startswith('```'):
            if line[3:].strip() == '':
                record = True
            else:
                record = False
                languages.add(line[3:].replace('\n', ''))
        if record:
            reg = "[^\sA-Za-z\u4e00-\u9fa5]"
        else:
            reg = "[^\s\u4e00-\u9fa5]"
        line = re.sub(reg, "", line)
        final += line
        final += '\n'
    return level_titles, languages, final

def construct_title(extracted_titles):
    head = Menu(0, "")
    last = {}
    last[0] = head
    for title in extracted_titles:
        t = Menu(len(title.split(" ")[0]), title.split(" ")[1])
        last[t.level] = t
        ll = t.level-1
        while last.get(ll) is None:
            ll -= 1
        last[ll].add_children(t)
    return head

def do_extract(input):
    level_titles, languages, final = preprocess(input)
    level_titles = json.dumps(to_json_str(construct_title(level_titles)), ensure_ascii=False)
    return level_titles, languages, final


if __name__ == '__main__':
    f = open("test.txt")
    input = f.read()
    level_titles, languages, final = do_extract(input)
    print(level_titles)
    # print( to_json_str(construct_title(level_titles)))
    # print(json.dumps(to_json_str(construct_title(level_titles)), ensure_ascii=False))