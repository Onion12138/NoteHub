from flask import Flask, jsonify, request
import extract_title_content_keywords
import judge_if_trash

app = Flask(__name__)


@app.route("/analyze", methods=['POST'])
def analyze():
    note = request.form.get("note")
    if note is None or note is "":
        return error()
    level_titles, titles, content, keywords, summary = extract_title_content_keywords.do_extract(note)
    if judge_if_trash.predict(content) is False:
        return error()
    data = {
        "summary": summary,
        "titles": titles,
        "keywords": keywords,
        "level_titles": level_titles,
        "tags": ""  # to complete
    }
    return success(data)


@app.route("/health")
def health():
    data = {
        "status": "UP"
    }
    return jsonify(data)


def success(data):
    result = {
        "code": 0,
        "msg": "success",
        "data": data
    }
    return jsonify(result)


def error():
    result = {
        "code": -1,
        "msg": "内容不存在或违规！"
    }
    return jsonify(result)

if __name__ == '__main__':
    app.run('0.0.0.0')
