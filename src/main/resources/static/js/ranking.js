$(document).ready(function() {
    getRankingAll(undefined, undefined, undefined);
})


// ========================================
// 리뷰어 랭킹 - 전체 보기
// ========================================
function getRankingAll(page, type, query) {
    $("#rankingList").empty()
    let data = {
        page: page,
        query: query,
        type: type
    };
    $.ajax({
        type: "GET",
        url: base_url + "/ranking",
        data: data,
        success: function (res) {
            console.log(res)
            let data = res["data"];
            let totalPage = res['totalPage']
            let page = res['page'];
            let size = res['size'];
            for (let i = 0; i < data.length; i++) {
                let id = data[i]["id"]
                let username = data[i]["username"];
                let nickname = data[i]["nickname"];
                let languages = data[i]["languages"];
                let answerCount = data[i]["answerCount"];
                let point = data[i]["point"];
                let ranking = i + 1 + size * page;

                let temp = ` <tr>
                                  <th scope="row">${ranking} 위</th>
                                  <td>${nickname} 님</td>
                                  <td>${languages}</td>
                                  <td>${answerCount}</td>
                                  <td>${point}</td>
                                  <td>
                                     <button onclick="questionConfirm(${id})" class="button is-info is-small">질문하기</button>
                                  </td>
                                </tr>`;
                $("#rankingList").append(temp);
            } // end-for

            addPageButton(totalPage, page, type, query);
        },
    });
}

function addPageButton(totalPage, page, type, query) {
    $('#ranking-list-pagination-ul').empty();
    console.log("addPage=",totalPage, page, type, query);
    for (let i=1;i<=totalPage;i++) {
        let html_button = ``;
        if (i===page+1) {
            html_button = `<li><a class="pagination-link is-current" onclick="movePage(${i}, ${type}, ${query})">${i}</a></li>`
        } else {
            if (query !== undefined && type !== undefined) {
                html_button = `<li><a class="pagination-link" onclick="movePage(${i}, '${type}', '${query}')">${i}</a></li>`
            } else {
                html_button = `<li><a class="pagination-link" onclick="movePage(${i}, ${type}, ${query})">${i}</a></li>`
            }
        }
        $('#ranking-list-pagination-ul').append(html_button);
    }
}

function movePage(page, type, query) {
    console.log("movePage=", page, type, query)
    getRankingAll(page, type, query);
}

// ========================================
// 검색하기
// ========================================
function searchReviewer() {
    let type = $("#search-reviewer-type").val();
    let query = $("#reviewer-search-input").val();
    getRankingAll(undefined, type, query);
}

function questionConfirm(reviewerId) {
    console.log(reviewerId);
}

