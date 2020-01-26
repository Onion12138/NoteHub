from flask import Flask, jsonify, request
import re
import jieba
from jieba.analyse import *
from textrank4zh import TextRank4Sentence
app = Flask(__name__)

@app.route("/analyze")
def analyze():
    text = request.form.get("text")
    print(text)
    level_titles, languages, processed_str = preprocess(text)
    keywords = extract_keyword(processed_str)
    summary = summarize(processed_str)
    data = {
        "titles": list(level_titles),
        "languages": list(languages),
        "keywords": keywords,
        "summary": summary
    }
    return jsonify(data)

@app.route("/health")
def health():
    data = {
        "status": "UP"
    }
    return jsonify(data)

def summarize(processed_str):
    tr4s = TextRank4Sentence()
    tr4s.analyze(text=processed_str, lower=True, source = 'no_stop_words')
    key_sentences = tr4s.get_key_sentences(num=10, sentence_min_len=2)
    return key_sentences

def preprocess(raw_str):
    text = raw_str.split("\n")
    record = True
    level_titles = set()
    languages = set()
    final = ""
    for line in text:
        if line.strip() == '':
            continue
        extract_title = re.findall('#{1,4} (.*)',line)
        if len(extract_title):
            level_titles.add(extract_title[0])
            continue
        if line.startswith('```'):
            if line[3:].strip() == '':
                record = True
            else:
                record = False
                languages.add(line[3:].replace('\n',''))
        if record:
            reg = "[^\sA-Za-z\u4e00-\u9fa5]"
        else:
            reg = "[^\s\u4e00-\u9fa5]"
        line = re.sub(reg,"",line)
        final += line
        final += '\n'
    return level_titles, languages, final

def extract_keyword(processed_str):
    keyword = {}
    tags = extract_tags(processed_str, topK=20, withWeight=True)
    rank = textrank(processed_str, topK=20, withWeight=True)
    max_tag = tags[0][1]
    min_tag = tags[-1][1]
    max_rank = rank[0][1]
    min_rank = rank[-1][1]
    for k, w in tags:
        if max_tag != min_tag:
            keyword[k] = (w-min_tag)/(max_tag-min_tag)
        else:
            keyword[k] = 0
    for k, w in rank:
        if max_rank != min_rank:
            if k in keyword.keys():
                keyword[k] += (w-min_rank)/(max_rank-min_rank)
            else:
                keyword[k] = (w-min_rank)/(max_rank-min_rank)
        else:
            if k in keyword.keys():
                keyword[k] += 0
            else:
                keyword[k] = 0
    return sorted(keyword.items(),key=lambda x:-x[1])[:10]

if __name__ == '__main__':
    app.run()
