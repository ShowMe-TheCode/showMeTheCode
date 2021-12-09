function go_back() {
    history.go(-1);
}

// let base_url = "http://aws-lb-1144110396.ap-northeast-2.elb.amazonaws.com"

// let subscribeUrl = "http://localhost:8080"
$(document).ready(function () {

    loginCheck()
    getQuestionList();
    getRanking();
    getRankingAll();
    getTag();

    $("ul.status").find('li').each(function (i, e) {
        if ($(this).data('status') == getParameterByName("status")) {
            $(this).addClass('active')
        }
        else(
            $(this).removeClass('active')
        )
    })

    $("ul.status li").click(function () {
        let URLSearch = new URLSearchParams(location.search);
        $("ul.status li").removeClass("active")
        $(this).addClass("active")
        let status = $(this).data('status')


        URLSearch.set('status', status)

        let newParam = URLSearch.toString();

        window.open(location.pathname + '?' + newParam, '_self')
    })
})

// ========================================
// 인기태그 목록
// ========================================
function getTag(){
    $.ajax({
        type: "GET",
        url: base_url+"/question/languages/count",
        success: function (res) {
            let tagname = "";
            let count = 0;
            let list = res.sort(function (a,b){
                return b.count - a.count;
            });
            for(tag in list){
                tagname = list[tag].languageName;
                count = list[tag].count;
                let temp_html = `<li class="popular-tags__tag ">

                            <button onclick="getQuestionListByLanguage('${tagname}')" class="ac-button is-sm is-solid is-gray e-popular-tag ac-tag ac-tag--blue "><span class="ac-tag__hashtag">#&nbsp;</span><span class="ac-tag__name">${tagname} [${count}]</span></button>
                            </li>`
                $('#tag-list').append(temp_html)
            }
        }
    })
}


// ========================================
// 언어별 코드리뷰 요청 목록보기
// ========================================
function getQuestionListByLanguage(language,page) {
    $('#reviewQuestionList').empty();
    $('#pagination-container').remove();
    let currentPage = page;

    if (!currentPage) {
        currentPage = 1
    }
    nextPage = parseInt(currentPage) + 1;
    $.ajax({
        type: "GET",
        url: base_url+"/question/language",
        data: {
            page: currentPage,
            language: language
        },
        success: function (res) {
            makeQuestionListByLanguage(language, res, currentPage);
        }
    })
}


// ========================================
// 코드리뷰 요청 목록보기
// ========================================
function getQuestionList() {
    $('#reviewQuestionList').empty();
    let currentPage = getParameterByName('page');
    let query = getParameterByName('query');
    let sort = getParameterByName('sortBy');
    let status = getParameterByName('status').toString().toUpperCase();
    let isAsc = getParameterByName('isAsc');
    let size = getParameterByName('size');

    if(!query){
        query = null;
    }
    if (!currentPage) {
        currentPage = 1
    }
    nextPage = parseInt(currentPage) + 1;
    $.ajax({
        type: "GET",
        url: base_url+"/questions",
        data: {
            page: currentPage,
            query: query,
            status: status
        },
        success: function (res) {

            console.log(res)
            makeQuestionList(res, currentPage);
        }
    })
}

function move_page(page){
    let URLSearch = new URLSearchParams(location.search);
    URLSearch.set('page',page)

    let newParam = URLSearch.toString();

    window.open(location.pathname + '?' + newParam,'_self')
    // location.href = "?query="+query
}


function reviewSearch(){
    let query = $('#review-search-input').val()
    let URLSearch = new URLSearchParams(location.search);
    URLSearch.set('query',query)

    let newParam = URLSearch.toString();

    window.open(location.pathname + '?' + newParam,'_self')
    // location.href = "?query="+query
}

function makeQuestionListByLanguage(language,res, currentPage) {
    let data = res['data']
    let pagination = `<nav id="pagination-container" class="pagination is-centered is-small" role="navagation" aria-label="pagination">`
    let totalpage = res.totalPage

    // 전체 페이지가 1개인 경우
    if (totalpage == 1) {
        pagination += `
                                    <ul class="pagination-list" id="pagingList">
                                        <li>
                                            <a class="pagination-link is-current" onclick="getQuestionListByLanguage('${language}',${1}) aria-label="1 페이지로 이동">
                                                1
                                            </a>
                                        </li>
                                    </ul>
                                </nav>
                            `
        // 전체 페이지가 1개 이상인 경우
    } else {
        if (currentPage < totalpage) {
            pagination += `<a class="pagination-next" onclick="getQuestionListByLanguage('${language}',${nextPage})">다음 페이지</a>`
        }
        pagination += `<ul class="pagination-list" id="pagingList">`
        for (let i = 1; i <= totalpage; i++) {
            if (totalpage == 11) {
                pagination += `<li>
                            <a class="pagination-link" onclick="getQuestionListByLanguage('${language}',${i})" aria-label="${i} 페이지로 이동">
                                ...
                            </a>
                        </li>`


                break;
            }
            // 현재 페이지 버튼에 대한 처리
            if (currentPage == i) {
                pagination += `
                                <li>
                                    <a class="pagination-link is-current" onclick="getQuestionListByLanguage('${language}',${i})" aria-label="${i} 페이지로 이동">
                                        ${i}
                                    </a>
                                </li>
                                `
            } else {
                pagination += `
                                <li>
                                    <a class="pagination-link" onclick="getQuestionListByLanguage('${language}',${i})" aria-label="${i} 페이지로 이동">
                                        ${i}
                                    </a>
                                </li>
                                `
            }

        }
        pagination += `</ul></nav>`
    }
    $('#community-body').append(pagination)

    for (let i = 0; i < data.length; i++) {
        let date = new Date(data[i].createdAt)
        let content = data[i].content
        content = content.toString().replace(/(<([^>]+)>)/ig,"").replace(/\r\n/g, "").slice(0,50)
        date = dateFormat(date)
        let li = `<li class="question-container">
                                <a onclick="showQuestionDetails(${data[i].reviewRequestId})">
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
<!--                                        <div class="question__tags">-->
<!--    -->
<!--                                        </div>-->
                                        <div class="question__info-footer">
                                            ${data[i].languageName} · ${date}  · ${data[i].status} 
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
                            </a></li>`

        $('#reviewQuestionList').append(li);

    }
}

function makeQuestionList(res, currentPage) {
    let data = res['data']
    let pagination = `<nav id="pagination-container" class="pagination is-centered is-small" role="navagation" aria-label="pagination">`
    let totalpage = res.totalPage
    console.log(totalpage)

    // 전체 페이지가 1개인 경우
    if (totalpage == 1) {
        pagination += `
                                    <ul class="pagination-list" id="pagingList">
                                        <li>
                                            <a class="pagination-link is-current" onclick="move_page(${1}) aria-label="1 페이지로 이동">
                                                1
                                            </a>
                                        </li>
                                    </ul>
                                </nav>
                            `
    // 전체 페이지가 1개 이상인 경우
    } else {
        console.log(currentPage,totalpage)
        if (currentPage < totalpage) {
            pagination += `<a class="pagination-next" onclick="move_page(${nextPage})">다음 페이지</a>`
        }
        pagination += `<ul class="pagination-list" id="pagingList">`
        for (let i = 1; i <= totalpage; i++) {
            if (totalpage == 11) {
                pagination += `<li>
                            <a class="pagination-link" onclick="move_page(${i})" aria-label="${i} 페이지로 이동">
                                ...
                            </a>
                        </li>`


                break;
            }
            // 현재 페이지 버튼에 대한 처리
            if (currentPage == i) {
                pagination += `
                                <li>
                                    <a class="pagination-link is-current" onclick="move_page(${i})" aria-label="${i} 페이지로 이동">
                                        ${i}
                                    </a>
                                </li>
                                `
            } else {
                pagination += `
                                <li>
                                    <a class="pagination-link" onclick="move_page(${i})" aria-label="${i} 페이지로 이동">
                                        ${i}
                                    </a>
                                </li>
                                `
            }

        }
        pagination += `</ul></nav>`
    }
    $('#community-body').append(pagination)

    for (let i = 0; i < data.length; i++) {
        let date = new Date(data[i].createdAt)
        let content = data[i].content
        content = content.toString().replace(/(<([^>]+)>)/ig,"").replace(/\r\n/g, "").slice(0,50)
        date = dateFormat(date)
        let li = `<li class="question-container">
                                <a onclick="showQuestionDetails(${data[i].reviewRequestId})">
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
<!--                                        <div class="question__tags">-->
<!--    -->
<!--                                        </div>-->
                                        <div class="question__info-footer">
                                            ${data[i].languageName} · ${date}  · ${data[i].status} 
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
                            </a></li>`

        $('#reviewQuestionList').append(li);

    }
}

function showQuestionDetails(id) {
    location.href = `details.html?id=${id}`
}


// ========================================
// 리뷰어 랭킹 - 전체 보기
// ========================================

function getRankingAll() {
    $('#rankingList').empty();
    let currentPage = getParameterByName('page');
    if (!currentPage) {
        currentPage = 1
    }
    nextPage = parseInt(currentPage) + 1;
    $.ajax({
        type: "GET",
        url: base_url+"/reviewer/rank",
        data: {
            page: currentPage
        },
        success: function (res) {

            let data = res['data']
            let pagination = `<nav class="pagination is-centered is-small" role="navagation" aria-label="pagination">`
            let totalpage = res.totalPage
            if (totalpage == 1) {
                pagination += `
                                    <ul class="pagination-list" id="pagingList">
                                        <li>
                                            <a class="pagination-link is-current" href="?page=1" aria-label="1 페이지로 이동">
                                                1
                                            </a>
                                        </li>
                                    </ul>
                                </nav>
                            `
            } else {
                pagination += `<a class="pagination-next" href="?page=${nextPage}">다음 페이지</a>
                                <ul class="pagination-list" id="pagingList">`

                for (let i = 1; i <= totalpage; i++) {
                    if (totalpage == 11) {
                        pagination += `<li>
                            <a className="pagination-link" href="?page=${i}" aria-label="${i} 페이지로 이동">
                                ...
                            </a>
                        </li>`


                        break;
                    }
                    if (currentPage == i) {
                        pagination += `
                                <li>
                                    <a class="pagination-link is-current" href="?page=${i}" aria-label="${i} 페이지로 이동">
                                        ${i}
                                    </a>
                                </li>
                                `
                    } else {
                        pagination += `
                                <li>
                                    <a class="pagination-link" href="?page=${i}" aria-label="${i} 페이지로 이동">
                                        ${i}
                                    </a>
                                </li>
                                `
                    }

                }
                pagination += `</ul></nav>`
            }
            $('#community-body2').append(pagination)

            for (let i = 0; i < data.length; i++) {



                let username = data[i]["username"]
                let nickname = data[i]["nickname"]
                let languages = data[i]["languages"];
                let answerCount = data[i]["answerCount"];
                let point = data[i]["point"];
                let ranking = i + 1

                let temp = ` <tr>
                                  <th scope="row">${ranking} 위</th>
                                  <td>${nickname} 님</td>
                                  <td>${languages}</td>
                                  <td>${answerCount}</td>
                                  <td>${point}</td>
                                </tr>`
                $('#rankingList').append(temp);
            } // end-for
        }
    })
}


// ========================================
// 리뷰어 랭킹 - 상위 5위 목록보기
// ========================================
function getRanking(){
    $.ajax({
        type: "GET",
        url: base_url+"/reviewer/top",
        data: {},
        success: function (res) {
            for (let i = 0; i < res.length; i++){
                let ranking = i + 1
                let username = res[i]["username"];
                let nickname = res[i]["nickname"];
                let languages = res[i]["languages"];
                let answerCount = res[i]["answerCount"];
                let point = res[i]["point"];

                let languages_html = `<span id="ranking-language">`;
                for (let i=0;i<languages.length;i++) {
                    if (i+1 === languages.length) languages_html += `<p>${languages[i]}</p>`
                    else languages_html += `<p>${languages[i]} , </p>`
                }
                languages_html += `</span>`

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
        }
    })
}


/**
 * 설정
 */

// ========================================
// ajax 요청시 token이 있다면 헤더에 추가하도록 설정
// ========================================
$.ajaxPrefilter(function (options, originalOptions, jqXHR) {
    if (sessionStorage.getItem('mytoken') != null) {
        jqXHR.setRequestHeader('Authorization', 'Bearer ' + sessionStorage.getItem('mytoken'));
    }
});

function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"), results = regex.exec(location.search);
    return results == null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}

function dateFormat(date) {
    let month = date.getMonth() + 1;
    let day = date.getDate();

    month = month >= 10 ? month : '0' + month;
    day = day >= 10 ? day : '0' + day;

    return date.getFullYear() + '.' + month + '.' + day;
}

function loginCheck() {
    // 인증이 된 경우
    if (sessionStorage.getItem("mytoken") != null) {
        $('#signinBtn').hide()
        $('#signupBtn').hide()
        $('#logoutBtn').show()
        $('#mypageBtn').show()

        $('#signinBtnMobile').hide()
        $('#signupBtnMobile').hide()
        $('#logoutBtnMobile').show()
        $('#myPageBtnMobile').show()


        $('#writeBtn').show()

        $('#changeReviewContentBtn').show();
        $('#changeReviewerBtn').show();
        $('#deleteReviewBtn').show();
    } else { // 인증이 되지 않은 경우
        $('#signinBtn').show()
        $('#signupBtn').show()
        $('#logoutBtn').hide()
        $('#mypageBtn').hide()

        $('#signinBtnMobile').show()
        $('#signupBtnMobile').show()
        $('#logoutBtnMobile').hide()
        $('#myPageBtnMobile').hide()

        $('#writeBtn').hide()
        $('#changeReviewContentBtn').hide();
        $('#changeReviewerBtn').hide();
        $('#deleteReviewBtn').hide();

    }
}