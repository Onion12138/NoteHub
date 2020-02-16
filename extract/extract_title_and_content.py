"""

@file: extract_title.py

@time: created at 2020/02/13, modified at 2.16

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


def construct_title(input):
    head = Menu(0, "")
    final = ""
    last = {0: head}
    found = 0
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
            final += re.sub(reg, "", line) + '\n'
            if len(re.findall(r"^#+ (.*)", line)):
                # print(line)
                length = len(line.split(" ")[0])
                t = Menu(length, line[length:])
                last[length] = t
                ll = length - 1
                while last.get(ll) is None:
                    ll -= 1
                last[ll].add_children(t)
                continue

    return head, final


def do_extract(input):
    level_titles, final = construct_title(input)
    level_titles = json.dumps(to_json_str(level_titles), ensure_ascii=False)
    return level_titles, final


if __name__ == '__main__':
    f = open("test.txt")
    input = f.read()
    level_titles, final = do_extract(input)
    print(level_titles)
    print(final)
    # print( to_json_str(construct_title(level_titles)))
    # print(json.dumps(to_json_str(construct_title(level_titles)), ensure_ascii=False))
