//let base_url = "http://smtc-env.eba-xa9v6fft.ap-northeast-2.elasticbeanstalk.com";
let base_url = ""

function go_back() {
	history.go(-1);
}

$(document).ready(function () {
	loginCheck();
	$("#reviewQuestionList").empty();
	getQuestionList(null);
	getRanking();
	getRankingAll();
	getTag();

	$("ul.status")
		.find("li")
		.each(function (i, e) {
			if ($(this).data("status") == getParameterByName("status")) {
				$(this).addClass("active");
			} else $(this).removeClass("active");
		});

	$("ul.status li").click(function () {
		let URLSearch = new URLSearchParams(location.search);
		$("ul.status li").removeClass("active");
		$(this).addClass("active");
		let status = $(this).data("status");

		URLSearch.set("status", status);

		let newParam = URLSearch.toString();

		window.open(location.pathname + "?" + newParam, "_self");
	});
});

// ========================================
// 코드리뷰 요청 목록보기
// ========================================
function getQuestionList(lastId) {
	$("#readMoreBtnBox").empty()
	let query = getParameterByName("query");
	let language = getParameterByName("language");
	let status = getParameterByName("status").toString().toUpperCase();

	if (!query) {
		query = null;
	}

	$.ajax({
		type: "GET",
		url: base_url + "/questions",
		data: {
			lastId: lastId,
			query: query,
			language: language,
			status: status,
		},
		success: function (res) {
			let lastId = res['lastId']
			let lastPage = res['lastPage']

			makeQuestionList(res, lastId, lastPage);
		},
	});
}

function reviewSearchByTitleAndContent() {
	let query = $("#review-search-input").val();
	let URLSearch = new URLSearchParams(location.search);
	URLSearch.delete("language")
	URLSearch.set("query", query);

	let newParam = URLSearch.toString();

	window.open(location.pathname + "?" + newParam, "_self");
}

function reviewSearchByLanguage(language) {
	let URLSearch = new URLSearchParams(location.search);
	URLSearch.delete("query")
	URLSearch.set("language", language);

	let newParam = URLSearch.toString();

	window.open(location.pathname + "?" + newParam, "_self");
}

function makeQuestionList(res, lastId, lastPage) {

	let data = res["data"];

	for (let i = 0; i < data.length; i++) {
		let date = new Date(data[i].createdAt);
		let content = data[i].content;
		content = content
			.toString()
			.replace(/(<([^>]+)>)/gi, "")
			.replace(/\r\n/g, "")
			.slice(0, 50);
		date = dateFormat(date);

		let li = `<li class="question-container">
                                <a onclick="showQuestionDetails(${data[i].questionId})">
                        <div class="question">
                            <div class="question__info">
                                        <div class="question__title">
                                            <h3 class="title__text">
                                                ${data[i].title}
                                                <span class="infd-icon title__icon">
                                                </span>
                                            </h3>
                                        </div>
                                        <p class="question__body">
                                            ${content}
                                        </p>
                                        <div class="question__info-footer">
                                           ${data[i].nickname} · ${data[i].languageName} · ${date}  · ${data[i].status} 
                                        </div>
                                    </div>
                                    <div class="question__additional-info">
                                        <div class="question__comment">
                                            <span class="comment__count">${data[i].commentCount}</span>
                                            <span class="comment__description">댓글수</span>
                                        </div>
    
                                        <button class="ac-button is-md is-text question__like e-like">
                                            <svg width="16" xmlns="http://www.w3.org/2000/svg" width="16" height="16"
                                                 viewBox="0 0 16 16">
                                                <path fill="#616568"
                                                      d="M9.333 13.605c-.328.205-.602.365-.795.473-.102.057-.205.113-.308.168h-.002c-.143.074-.313.074-.456 0-.105-.054-.208-.11-.31-.168-.193-.108-.467-.268-.795-.473-.655-.41-1.53-1.007-2.408-1.754C2.534 10.382.667 8.22.667 5.676c0-2.308 1.886-4.01 3.824-4.01 1.529 0 2.763.818 3.509 2.07.746-1.252 1.98-2.07 3.509-2.07 1.938 0 3.824 1.702 3.824 4.01 0 2.545-1.867 4.706-3.592 6.175-.878.747-1.753 1.344-2.408 1.754z"/>
                                            </svg>
                                            0
                                        </button>
                                    </div>
                                </div>
                            </a></li>`;

		$("#reviewQuestionList").append(li);
	}

	if (!lastPage) {
		let readMoreBtn = `<button onclick="getQuestionList(${lastId})"  class="button is-large is-fullwidth">더보기</button>`
		$("#readMoreBtnBox").append(readMoreBtn)
	}
}

function showQuestionDetails(id) {
	location.href = `details.html?id=${id}`;
}

// ========================================
// 리뷰어 랭킹 - 전체 보기
// ========================================
function getRankingAll() {
	$("#rankingList").empty();

	$.ajax({
		type: "GET",
		url: base_url + "/reviewers/rank",
		success: function (res) {
			let data = res["data"];

			for (let i = 0; i < data.length; i++) {
				let username = data[i]["username"];
				let nickname = data[i]["nickname"];
				let languages = data[i]["languages"];
				let answerCount = data[i]["answerCount"];
				let point = data[i]["point"];
				let ranking = i + 1;

				let temp = ` <tr>
                                  <th scope="row">${ranking} 위</th>
                                  <td>${nickname} 님</td>
                                  <td>${languages}</td>
                                  <td>${answerCount}</td>
                                  <td>${point}</td>
                                </tr>`;
				$("#rankingList").append(temp);
			} // end-for
		},
	});
}

// ========================================
// 인기태그 목록
// ========================================
function getTag() {
	$.ajax({
		type: "GET",
		url: base_url + "/questions/languages/count",
		success: function (res) {
			let tagname = "";
			let count = 0;
			let list = res.sort(function (a, b) {
				return b.count - a.count;
			});
			for (tag in list) {
				tagname = list[tag]['languageName'];
				count = list[tag].count;
				let temp_html = `<li class="popular-tags__tag ">

                            <button onclick="reviewSearchByLanguage('${tagname}')" class="ac-button is-sm is-solid is-gray e-popular-tag ac-tag ac-tag--blue "><span class="ac-tag__hashtag">#&nbsp;</span><span class="ac-tag__name">${tagname} [${count}]</span></button>
                            </li>`;
				$("#tag-list").append(temp_html);
			}
		},
	});
}

// ========================================
// 리뷰어 랭킹 - 상위 5위 목록보기
// ========================================
function getRanking() {
	$.ajax({
		type: "GET",
		url: base_url + "/reviewers/top",
		data: {},
		success: function (res) {

			console.log(res);

			for (let i = 0; i < res.length; i++) {
				let ranking = i + 1;
				let username = res[i]["username"];
				let nickname = res[i]["nickname"];
				let languages = res[i]["languages"];
				let answerCount = res[i]["answerCount"];
				let point = res[i]["point"];

				let languages_html = `<span id="ranking-language">`;
				for (let i = 0; i < languages.length; i++) {
					if (i + 1 === languages.length)
						languages_html += `<p>${languages[i]}</p>`;
					else languages_html += `<p>${languages[i]} , </p>`;
				}
				languages_html += `</span>`;

				let tmp_html = `<li class="">
                                    <div>
                                        <span>${ranking}위</span>
                                        <span>${nickname} 님</span>
                                        ${languages_html}
                                    </div>
                                    <div>
                                        <span id="ranking-answer">답변수 ${answerCount}</span>
                                        <span id="ranking-point">포인트 ${point}</span>
                                    </div>
                                </li>`;
				$("#top-ranking").append(tmp_html);
			}
		},
	});
}

/**
 * 설정
 */

// ========================================
// ajax 요청시 token이 있다면 헤더에 추가하도록 설정
// ========================================
$.ajaxPrefilter(function (options, originalOptions, jqXHR) {
	if (localStorage.getItem("mytoken") != null) {
		jqXHR.setRequestHeader(
			"Authorization",
			"Bearer " + localStorage.getItem("mytoken")
		);
	}
});

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

function loginCheck() {
	// 인증이 된 경우
	if (localStorage.getItem("mytoken") != null) {
		$("#signinBtn").hide();
		$("#signupBtn").hide();
		$("#logoutBtn").show();
		$("#mypageBtn").show();

		$("#signinBtnMobile").hide();
		$("#signupBtnMobile").hide();
		$("#logoutBtnMobile").show();
		$("#myPageBtnMobile").show();

		$("#writeBtn").show();

		$("#changeReviewContentBtn").show();
		$("#changeReviewerBtn").show();
		$("#deleteReviewBtn").show();
	} else {
		// 인증이 되지 않은 경우
		$("#signinBtn").show();
		$("#signupBtn").show();
		$("#logoutBtn").hide();
		$("#mypageBtn").hide();

		$("#signinBtnMobile").show();
		$("#signupBtnMobile").show();
		$("#logoutBtnMobile").hide();
		$("#myPageBtnMobile").hide();

		$("#writeBtn").hide();
		$("#changeReviewContentBtn").hide();
		$("#changeReviewerBtn").hide();
		$("#deleteReviewBtn").hide();
	}
}
