$(document).ready(function () {
	let id = getParameterByName("id");
	getDetails(id);
});

/**
 * 상세정보
 */
function getDetails(id) {

	$.ajax({
		type: "GET",
		url: base_url + `/questions/${id}`,
		contentType: "application/json;charset-utf-8;",
		success: function (res) {

			let questionId = res['questionId']
			let questionUserId = res['questionUserId']
			let answer = res['answer']
			let date = new Date(res['createdAt']);
			let title = `<h1>${res['title']}</h1>`
			let status = res['status'];
			let comments = res["comments"];

			date = dateFormat(date);

			$("#request-title").append(title);
			$("#user-name").html(res['nickname']);
			$("#created-at").html(`&nbsp;· ` + date);
			$("#question-content").html(res['content']);


			$("#sub-info__content")
				.append(`<button class="ac-button is-sm is-solid is-gray  ac-tag ac-tag--blue ">
								<span class="ac-tag__hashtag">#&nbsp;</span><span class="ac-tag__name">${res['languageName']}</span></button>`);

			$("#question-status").text(status);

			$("#question-comment-content-box").empty();
			if (comments.length > 0) {
				$("#comment-section").show();
				addCommentHtml(comments);
			}

			if (answer != null) { // 답변이 있는 경우 편집이 불가능하도록 <p> 태그 내에 답변 내용을 랜더링

				let answer = res['answer']
				$('#answer-date').html(answer['createdAt'])
				$('#answer-nickname').html(answer['nickname'])
				$('#answer-content').html(answer['content']).show()

				$('#send-answer-btn-box').hide()
				$('#answer-markdown-box').hide()
			} else { // 답변이 없는 경우 답변이 가능하도록 textarea (markdown폼)을 랜더링
				$('#answer-content').hide()

				$('#answer-markdown-box').show()
				$('#send-answer-btn-box').show()
			}
		},
	});
}

function addComment() {
	if (localStorage.getItem("mytoken") == null) {
		return alert("로그인 후 이용해주세요.");
	}
	let questionId = getParameterByName("id");
	let content = CKEDITOR.instances["comment-markdown"].getData();

	let data = { content: content };

	$.ajax({
		type: "POST",
		url: base_url + `/comments/${questionId}`,
		contentType: "application/json;charset=utf-8;",
		data: JSON.stringify(data),
		success: function (res) {
			alert("댓글작성 완료");
			window.location.reload();
		},
	});
}

/**
 * 답변하기
 */
function sendAnswer() {
	let questionId = getParameterByName("id");
	let content = CKEDITOR.instances["answer-markdown"].getData();

	let data = {
		content: content,
	};

	$.ajax({
		type: "POST",
		url: base_url + `/answers/${questionId}`,
		contentType: "application/json;charset=utf-8",
		data: JSON.stringify(data),
		success: function (res) {
			alert("리뷰 작성을 완료했습니다.");
			window.location.reload();
		},
	});
}

/**
 * 거절하기
 */
function reject() {
	let id = getParameterByName("id");

	$.ajax({
		type: "POST",
		url: base_url + `/answers/${id}/reject`,
		success: function (res) {
			alert("리뷰요청을 거절했습니다.");
			location.href = "mypage.html";
		},
	});
}

function getParameterByName(name) {
	name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
	var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
		results = regex.exec(location.search);
	return results == null
		? ""
		: decodeURIComponent(results[1].replace(/\+/g, " "));
}

function dateFormat(date) {
	let month = date.getMonth() + 1;
	let day = date.getDate();

	month = month >= 10 ? month : "0" + month;
	day = day >= 10 ? day : "0" + day;

	return date.getFullYear() + "." + month + "." + day;
}
