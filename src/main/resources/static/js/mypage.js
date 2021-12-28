$(document).ready(function () {
	let mytoken = localStorage.getItem("mytoken");
	let myAuthority = localStorage.getItem("myAuthority");

	$("#mypage-menu-list").empty();
	if (mytoken != null && myAuthority != null) {
		if (myAuthority === "ROLE_USER") {
			let tmp_html = `<li id="menu-request">
                                    <a class="btn_wrap  " onclick="myRequestQuestionList('ALL')">
                                        <span>내가 요청한 코드리뷰 목록</span>
                                    </a>
                                </li>`;
			$("#mypage-menu-list").append(tmp_html);
		} else if (myAuthority === "ROLE_REVIEWER") {
			let tmp_html = `<li id="menu-received">
                                    <a class="btn_wrap " onclick="myReceivedQuestionList('ALL')">
                                        <span>나에게 요청된 코드리뷰 목록</span>
                                    </a>
                                </li>
                                <li id="menu-request">
                                    <a class="btn_wrap  " onclick="myRequestQuestionList('ALL')">
                                        <span>내가 요청한 코드리뷰 목록</span>
                                    </a>
                                </li>`;
			$("#mypage-menu-list").append(tmp_html);
		}
	}
});

// ========================================
// 요청받은 코드리뷰 목록 조회
// ========================================
function myReceivedQuestionList(condition) {
	$("#condition-box").empty();
	let tmp_html = `<button onclick="myReceivedQuestionList('ALL')" class="button is-link is-rounded">전체보기</button>&nbsp;&nbsp;
                    <button onclick="myReceivedQuestionList('SOLVE')" class="button is-link is-rounded">해결됨</button>&nbsp;&nbsp;
                    <button onclick="myReceivedQuestionList('UNSOLVE')" class="button is-link is-rounded">미해결</button>`;
	$("#condition-box").append(tmp_html);

	$.ajax({
		type: "GET",
		url: base_url + `/reviewers/questions?status=${condition}`,
		success: function (res) {
			console.log(res);
			$("#question-list").empty();
			let reviews = res["data"];
			addReceivedReviewList(reviews);
		},
	});
}

// ========================================
// 내가 요청한 코드리뷰 목록
// ========================================
function myRequestQuestionList(condition) {
	$("#condition-box").empty();
	let tmp_html = `<button onclick="myRequestQuestionList('ALL')" class="button is-link is-rounded">전체보기</button>&nbsp;&nbsp;
                    <button onclick="myRequestQuestionList('SOLVE')" class="button is-link is-rounded">해결됨</button>&nbsp;&nbsp;
                    <button onclick="myRequestQuestionList('UNSOLVE')" class="button is-link is-rounded">미해결</button>`;
	$("#condition-box").append(tmp_html);

	$.ajax({
		type: "GET",
		url: base_url + `/users/requests?status=${condition}`,
		success: function (res) {
			$("#question-list").empty();
			let reviews = res["data"];
			addRequestReviewList(reviews);
		},
	});
}

//==========================//
// 나에게 요청된 리뷰목록 랜더링
//==========================//
function addReceivedReviewList(reviews) {
	if (reviews.length > 0) {
		for (let i = 0; i < reviews.length; i++) {
			let tmp_html = `<li class="question-container">
                                    <a onclick="showQuestionDetailsForReview('${reviews[i].reviewRequestId}')">
                                        <div class="question-list__question  e-detail">
                                            <div class="question__info">
                                                <div class="question__info-cover">
                                                    <div class="question__info--main">
                                                        <div class="question__title">
                                                            <h3 class="title__text">
                                                                <span>${reviews[i].title}</span> <span id="question-status" class="tag is-dark">${reviews[i].status}</span>
                                                            </h3>
                                                        </div>
                                                        <p class="question__body">
                                                           ${reviews[i].content}
                                                        </p>


                                                    </div>
                                                    <div class="question__info-footer">
                                                        <div class="footer__cover">
                                                            <span class="footer__name">${reviews[i].nickname}</span>
                                                            <span class="footer__dot"> ·</span>
                                                            <span class="footer__info">${reviews[i].createdAt}</span>
                                                            <span class="footer__dot"> ·</span>
                                                            <span class="footer__info">${reviews[i].languageName}</span>
                                                        </div>

                                                        <div class="footer__additional-info">
                                                            <div class="additional-info ">
                                                                <span class="additional-info__icon"><span class="infd-icon icon-box-16"><svg width="16" xmlns="http://www.w3.org/2000/svg" width="16" height="16"  viewBox="0 0 384 512"><path fill="#616568"  d="M336 0H48C21.49 0 0 21.49 0 48v464l192-112 192 112V48c0-26.51-21.49-48-48-48zm16 456.287l-160-93.333-160 93.333V48c0-8.822 7.178-16 16-16h288c8.822 0 16 7.178 16 16v408.287z"></path></svg></span></span>
                                                                <span class="additional-info__count ">${reviews[i].commentCount}</span>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>

                                            </div>
                                        </div>
                                    </a>
                                </li>`;
			$("#question-list").append(tmp_html);
		}
	} else {
		let tmp_html = `<p>조회된 결과가 없습니다.</p>`;
		$("#question-list").append(tmp_html);
	}
}

//==========================//
// 내가 요청한 리뷰목록 랜더링
//==========================//
function addRequestReviewList(reviews) {
	if (reviews.length > 0) {
		for (let i = 0; i < reviews.length; i++) {
			let tmp_html = `<li class="question-container">
                                    <a onclick="showQuestionDetails('${reviews[i].reviewRequestId}')">
                                        <div class="question-list__question  e-detail">
                                            <div class="question__info">
                                                <div class="question__info-cover">
                                                    <div class="question__info--main">
                                                        <div class="question__title">
                                                            <h3 class="title__text">
                                                                <span>${reviews[i].title}</span> <span id="question-status" class="tag is-dark">${reviews[i].status}</span>
                                                            </h3>
                                                        </div>
                                                        <p class="question__body">
                                                           ${reviews[i].content}
                                                        </p>


                                                    </div>
                                                    <div class="question__info-footer">
                                                        <div class="footer__cover">
                                                            <span class="footer__name">${reviews[i].nickname}</span>
                                                            <span class="footer__dot"> ·</span>
                                                            <span class="footer__info">${reviews[i].createdAt}</span>
                                                            <span class="footer__dot"> ·</span>
                                                            <span class="footer__info">${reviews[i].languageName}</span>
                                                        </div>

                                                        <div class="footer__additional-info">
                                                            <div class="additional-info ">
                                                                <span class="additional-info__icon"><span class="infd-icon icon-box-16"><svg width="16" xmlns="http://www.w3.org/2000/svg" width="16" height="16"  viewBox="0 0 384 512"><path fill="#616568"  d="M336 0H48C21.49 0 0 21.49 0 48v464l192-112 192 112V48c0-26.51-21.49-48-48-48zm16 456.287l-160-93.333-160 93.333V48c0-8.822 7.178-16 16-16h288c8.822 0 16 7.178 16 16v408.287z"></path></svg></span></span>
                                                                <span class="additional-info__count ">${reviews[i].commentCount}</span>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>

                                            </div>
                                        </div>
                                    </a>
                                </li>`;
			$("#question-list").append(tmp_html);
		}
	} else {
		let tmp_html = `<p>조회된 결과가 없습니다.</p>`;
		$("#question-list").append(tmp_html);
	}
}

function showQuestionDetails(id) {
	location.href = `details.html?id=${id}`;
}

function showQuestionDetailsForReview(id) {
	location.href = `answer.html?id=${id}`;
}

function getParameterByName(name) {
	name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
	var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
		results = regex.exec(location.search);
	return results == null
		? ""
		: decodeURIComponent(results[1].replace(/\+/g, " "));
}
