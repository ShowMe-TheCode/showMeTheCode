$(document).ready(function () {
	getDetails();
});

/**
 * 상세정보
 */
function getDetails() {
	let id = getParameterByName("id");

	$.ajax({
		type: "GET",
		url: base_url + `/questions/${id}`,
		success: function (res) {
			console.log(res);
			let questionId = res["questionId"];
			let username = res["username"];
			let title = res["title"];
			let content = res["content"];
			let status = res["status"];
			let createdAt = res["createdAt"];
			let date = dateFormat(new Date(createdAt));

			let answerId = res["answerId"];
			let answerContent = res["answerContent"];

			$("#request-title").html(title);
			$("#user-name").html(username);
			$("#created-at").html(date);
			$("#received_content").html(content);

			$("#question-status").text(status);

			// 리뷰요청의 상태가 [해결됨] 인 경우에만 거절하기 버튼을 활성화
			if (status === "해결됨" || status === "평가됨") {
				$("#rejectBtn").hide();
			} else {
				$("#rejectBtn").show();
			}

			// 답변이 있는 경우 편집이 불가능하도록 <p> 태그 내에 답변 내용을 랜더링
			if (answerContent) {
				$("#content-answer-markdown-box").hide();
				$("#addAnswerBtn").hide();
				$("#content-answer-text").show();
				$("#content-answer-text").html(answerContent);
				$("#question-comment-form-box2").show();
				$("#question-comment-form-box").hide();
				$("#answer-section2").show();
				$("#answer-content2").html(answerContent);

				// 답변이 없는 경우 답변이 가능하도록 textarea (markdown폼)을 랜더링
			} else {
				$("#addAnswerBtn").show();
				$("#content-answer-markdown-box").show();
				$("#content-answer-text").hide();
				$("#question-comment-form-box2").hide();
				$("#answer-section2").hide();
			}
		},
	});
}

function addComment2() {
	if (localStorage.getItem("mytoken") == null) {
		return alert("로그인 후 이용해주세요.");
	}
	let questionId = getParameterByName("id");
	let content = CKEDITOR.instances["content-answer2"].getData();

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
function addAnswer() {
	let questionId = getParameterByName("id");
	let content = CKEDITOR.instances["content-answer"].getData();

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
			console.log(res);
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
