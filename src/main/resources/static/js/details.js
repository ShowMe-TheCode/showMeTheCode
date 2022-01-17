
let g_reviewerId = null;
let g_questionUserId = null;

$(document).ready(function () {
	let id = getParameterByName("id");
	getDetails(id);
	if (localStorage.getItem("mytoken") != null && localStorage.getItem("userId") != null) {
		g_questionUserId = localStorage.getItem("userId")
	}
});

// ========================================
// 상세내용 랜더링
// ========================================
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

			date = dateFormat(date);

			g_reviewerId = res['answerUserId'];

			$("#request-title").append(title);
			$("#user-name").html(res.nickname);
			$("#created-at").html(`&nbsp;· ` + date);
			$("#content").html(res.content);


			$("#sub-info__content")
				.append(`<button class="ac-button is-sm is-solid is-gray  ac-tag ac-tag--blue ">
								<span class="ac-tag__hashtag">#&nbsp;</span><span class="ac-tag__name">${res['languageName']}</span></button>`);

			let status = res.status;
			$("#request-status").text(status);

			if (answer != null) myQuestion(questionUserId, status, answer['answerId'])
			else myQuestion(questionUserId, status, null)

			$("#question-comment-content-box").empty();
			let comments = res["comments"];
			if (comments.length > 0) {
				$("#comment-section").show();
				addCommentHtml(comments);
			}

			// 답변 랜더링 여부확인 후 랜더링 메서드 호출
			if (answer == null) {
				$("#answer-section").hide();
			} else {
				addAnswerHtml(answer);
				$("#answer-section").show();
			}
		},
	});
}

function myQuestion(currentQuestionUserId, questionStatus, answerId) {

	if (localStorage.getItem("userId") == null) return;

	let userId = localStorage.getItem("userId")
	if (userId.toString() === currentQuestionUserId.toString()) {
		if (questionStatus === "미해결") {
			$("#changeReviewContentBtn").show();
			$("#changeReviewerBtn").show();
			$("#deleteReviewBtn").show();
		} else if (questionStatus === "해결됨") {
			$("#changeReviewContentBtn").hide();
			$("#changeReviewerBtn").hide();
			$("#deleteReviewBtn").hide();
			let tmp_html = `<div class="flex-row feature__status e-status e-hover-toggle" data-status="1">
								<button onclick="showEvaluateForm('${answerId}')" class="ac-button is-md is-solid is-primary button-rounded undefined">평가하기</button>
							</div>`
			$("#request-status-box").append(tmp_html);
		} else if (questionStatus === "평가됨") {
			$("#changeReviewContentBtn").hide();
			$("#changeReviewerBtn").hide();
			$("#deleteReviewBtn").hide();
		}
	} else {
		console.log("주인아님")
		$("#changeReviewContentBtn").hide();
		$("#changeReviewerBtn").hide();
		$("#deleteReviewBtn").hide();
		$("#evaluateBtnBox").empty()
	}
}


// ========================================
// 댓글 랜더링
// ========================================
function addCommentHtml(comments) {
	let size = comments.length;

	$("#question-comment-qty").html(`총 ${size}개 댓글이 달렸습니다.`);

	for (let i = 0; i < size; i++) {
		let commentId = comments[i]["commentId"];
		let userId = comments[i]["userId"];
		let username = comments[i]["username"];
		let nickname = comments[i]["nickname"];
		let content = comments[i]["content"];
		let createdAt = comments[i]["createdAt"];

		let tmp_html = `<div class="comment__index">${i + 1}</div>
                    <div class="comment__card">
                        <div class="comment__header flex-row">
                            <img class="comment__user-profile"
                                 src="./images/default_profile.png"
                                 alt="[아이디] 프로필">
                            <div class="flex-column">
                                <div class="flex-row" id="question-comment-nickname">
                                    <a href="/users/@deeplyrooted" class="comment__user-name">${nickname}</a>
                                </div>
                                <span class="comment__updated-at">${createdAt}</span>
                            </div>
                        </div>
                        <div class="comment__body markdown-body">
                            <p id="question-comment-content">
								${content}
							</p>
                        </div>
                    </div>`;
		$("#question-comment-content-box").append(tmp_html);
	}
}

// ========================================
// 답변 랜더링
// ========================================
function addAnswerHtml(answer) {
	let answerId = answer["answerId"];
	let questionId = answer["questionId"];
	let username = answer["username"];
	let nickname = answer["nickname"];

	let content = answer["content"];
	let point = answer["point"];
	let createdAt = answer["createdAt"];

	$("#answer-nickname").html(nickname);
	$("#answer-date").html(dateFormat(new Date(createdAt)));
	$("#answer-content").html(content);
}

// ========================================
// 댓글등록
// ========================================
function addComment() {
	if (localStorage.getItem("mytoken") == null) {
		return alert("로그인 후 이용해주세요.");
	}
	let questionId = getParameterByName("id");
	let content = CKEDITOR.instances["content-comment"].getData();

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

// ========================================
// 평가하기
// ========================================
function showEvaluateForm(answerId) {
	temp_html = `<div id="eval_modal" class="modal ">
					<div class="dimmed"></div>
					  <article class="sign-in-modal">
						<span onclick="close_eval_modal()" class="e-close header__close-button">
						  <svg width="16px" xmlns="http://www.w3.org/2000/svg" height="12" viewBox="0 0 12 12"><path fill="#3E4042" fill-rule="evenodd" d="M.203.203c.27-.27.708-.27.979 0L6 5.02 10.818.203c.27-.27.709-.27.98 0 .27.27.27.708 0 .979L6.978 6l4.818 4.818c.27.27.27.709 0 .98-.27.27-.709.27-.979 0L6 6.978l-4.818 4.818c-.27.27-.709.27-.98 0-.27-.27-.27-.709 0-.979L5.022 6 .203 1.182c-.27-.27-.27-.709 0-.98z" clip-rule="evenodd"></path></svg>
						</span>
						<span class="header__logo">
						  <h2 style="margin: auto">평가하기</h2>
						</span>
						<h5>코드리뷰에 대한 평가를 해주세요 !!</h5>
						<div style="height: 150px">
                        <span class="star">
                          ★★★★★
                          <span style="margin: auto;">★★★★★</span>
                          <input id="star_value" type="range" oninput="drawStar(this)" value="1" step="1" min="0" max="10">
                        </span>
                        </div>
                        <button type="button" onclick="evaluateAnswer(${answerId})" class="ac-button is-md is-solid is-primary form__button e-sign-in">평가하기</button>
				
					  </article>
					  </div>
                   </div>`;

	$("body").append(temp_html);
}

function evaluateAnswer(answerId) {
	console.log("evaluate 호출 = " + answerId)
	let questionId = getParameterByName("id");
	console.log(questionId)
	let slider = document.getElementById("star_value");
	console.log(slider)
	let point = slider.value / 2;
	console.log(point)

	let data = { point: point };

	$.ajax({
		type: "POST",
		url: base_url + `/answers/eval/${questionId}/${answerId}`,
		contentType: "application/json;charset=utf-8;",
		data: JSON.stringify(data),
		success: function (res) {
			alert("답변에 대한 평가를 완료했습니다.");
			location.href = "mypage.html";
		},
	});
}

function close_eval_modal() {
	$("#eval_modal").remove();
}

function drawStar(target) {
	document.querySelector(`.star span`).style.width = `${target.value * 10}%`;
}

// ========================================
// 리뷰어 변경 모달폼 띄우기
// ========================================
function showChangeReviewerForm(languageName) {
	temp_html = `<div id="change-reviewer-modal" class="modal ">
                    <div class="dimmed"></div>
                      <article class="sign-in-modal">
                        <span onclick="close_change_reviewer_modal()" class="e-close header__close-button">
                          <svg width="16px" xmlns="http://www.w3.org/2000/svg" height="12" viewBox="0 0 12 12"><path fill="#3E4042" fill-rule="evenodd" d="M.203.203c.27-.27.708-.27.979 0L6 5.02 10.818.203c.27-.27.709-.27.98 0 .27.27.27.708 0 .979L6.978 6l4.818 4.818c.27.27.27.709 0 .98-.27.27-.709.27-.979 0L6 6.978l-4.818 4.818c-.27.27-.709.27-.98 0-.27-.27-.27-.709 0-.979L5.022 6 .203 1.182c-.27-.27-.27-.709 0-.98z" clip-rule="evenodd"></path></svg>
                        </span>
                        <span class="header__logo">
                          <img src="./images/header_logo.png" style=" margin: auto" />
                        </span>
                        <div class="form__item">
                            <label class="form__label" for="language-input">언어이름으로 리뷰어 검색</label>
                            <div class="ac-input-with-item--large question-modal__language ">
                                <input id="language-input" value="" data-kv="language" type="text" placeholder="언어를 입력해주세요.">
                                <button class="button is-primary" onclick="findReviewer()">확인</button>
                            </div>
                        </div>
                        
                                       
                        <div id="select-reviewer-box" class="form__item is-hidden">
                            <label class="form__label" for="select-reviewer">리뷰어 지정</label>
                            <div class="select is-fullwidth">
                                <select id="select-reviewer">
        
                                </select>
                            </div>
                        </div>        
                        
                         <footer class="modal-card-foot">
                            <button onclick="close_change_reviewer_modal()"
                                    class="ac-button is-lg is-outlined is-gray question-modal__button--cancel e-cancel-question-modal">
                                취소
                            </button>
            
                            <button id="send"
                                    class="ac-button is-lg is-solid is-primary question-modal__button--cancel e-submit-question-modal"
                                    onclick="changeReviewer()">
                                변경
                            </button>
                        </footer>
                      </article>
                    </div>`;

	$("body").append(temp_html);
}

function close_change_reviewer_modal() {
	$("#change-reviewer-modal").remove();
}

// ========================================
// 언어이름으로 리뷰어 찾기
// ========================================
function findReviewer() {
	$("#select-reviewer").empty();
	let query = $("#language-input").val();

	if (query != "") {
		$.ajax({
			type: "GET",
			url: base_url + `/reviewers/language?language=${query}`,
			success: function (res) {
				$("#select-reviewer").append("<option>리뷰어를 선택하세요</option>");

				for (let i = 0; i < res.length; i++) {
					let id = res[i]["id"];
					let username = res[i]["username"];
					let nickname = res[i]["nickname"];
					let answerCount = res[i]["answerCount"]; // 답변수
					let point = res[i]["point"];

					let option_html = `<option value=${id}>
                                    <span>${nickname}</span>&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;
                                    <span>답변수: ${answerCount}</span>&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;
                                    <span>평균점수: ${point}</span>
                                </option>`;

					$("#select-reviewer").append(option_html);
				}
				$("#select-reviewer-box").removeClass("is-hidden");
			},
		});
	} else {
		alert("해당 언어의 리뷰어가 존재하지 않습니다.");
	}
}

// ========================================
// 리뷰어 변경
// ========================================
function changeReviewer() {
	let newReviewerId = $("#select-reviewer option:selected").val();
	let questionId = getParameterByName("id");

	if (g_reviewerId.toString() === newReviewerId.toString()) {
		alert("기존 리뷰어와 동일한 리뷰어입니다.");
	} else {
		let data = { newReviewerId: newReviewerId };

		$.ajax({
			type: "POST",
			url: base_url + `/questions/${questionId}/reviewer/${g_reviewerId}`,
			contentType: "application/json;charset=utf-8",
			data: JSON.stringify(data),
			success: function (res) {
				console.log(res);
				alert("리뷰어를 변경했습니다.");
				window.location.reload();
			},
		});
	}
}

// ========================================
// 질문삭제
// ========================================
function deleteReviewConfirm() {
	if (confirm('삭제하시겠습니까?')) {
		deleteReview()
	} else {
		// no click
	}
}

function deleteReview() {
	let questionId = getParameterByName("id");
	$.ajax({
		type: "DELETE",
		url: base_url + `/questions/${questionId}`,
		success: function (res) {
			alert("리뷰요청 삭제했습니다.");
			// location.href = "mypage.html";
			history.go(-1);
		},
	});
}



// ========================================
// 질문 수정 모달 폼
// ========================================
function showEditModalForm() {
	let content = $("#content").html();
	let title = $("#request-title").text();

	editModal = `<div id="edit_modal" class="modal">
                        <div class="dimmed"></div>
                        <section class="modal-card question-modal">
                            <div class="question-modal__form e-form-question-modal" onsubmit="return false">
                                <div class="modal-card-body">
                                    <div class="form__item">
                                        <label class="form__label" for="title-input">제목</label>
                                        <div class="ac-input-with-item--large question-modal__title ">
                                            <input id="title-input" data-kv="title" type="text" value="${title}">
                                        </div>
                                    </div>
                                    <div class="form__item">
                                        <label class="form__label">내용</label>
                    
                                        <textarea name="modify-contents" class="form-control" id="modify-contents">
                                            ${content}
                                        </textarea>
                    
                                    </div>
                                </div>
                    
                                <footer class="modal-card-foot">
                    
                                    <button onclick="close_edit_modal()" id="close_modal"
                                            class="ac-button is-lg is-outlined is-gray question-modal__button--cancel e-cancel-question-modal">
                                        취소
                                    </button>
                    
                                    <button id="send"
                                            class="ac-button is-lg is-solid is-primary question-modal__button--cancel e-submit-question-modal"
                                            onclick="editReview()">
                                        저장
                                    </button>
                                </footer>
                            </div>
                        </section>
                    </div>`;

	$("#title-input").html(title);
	$("#modify-contents").html(content);
	$("body").append(editModal);

	CKEDITOR.replace("modify-contents", {
		fontSize_defaultLabel: "12",
		skin: "minimalist",
		language: "ko",
	});
}

function close_edit_modal() {
	$("#edit_modal").remove();
}

// ========================================
// 질문 수정
// ========================================
function editReview() {
	let questionId = getParameterByName("id");
	let content = CKEDITOR.instances["modify-contents"].getData();
	let title = $("#title-input").val();
	let data = {
		title: title,
		content: content,
	};
	console.log(title, content);

	$.ajax({
		type: "PUT",
		url: base_url + `/questions/${questionId}`,
		contentType: "application/json;charset=utf-8;",
		data: JSON.stringify(data),
		success: function (res) {
			alert("리뷰요청을 수정했습니다.");
			location.href = "mypage.html";
		},
	});
}

// ========================================
// 쿼리 파라미터 받아오기
// ========================================
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
