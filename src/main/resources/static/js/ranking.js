$(document).ready(function() {
    getRankingAll()
})


// ========================================
// 리뷰어 랭킹 - 전체 보기
// ========================================
function getRankingAll() {
    //$("#rankingList").empty();

    $.ajax({
        type: "GET",
        url: base_url + "/ranking",
        success: function (res) {
            console.log(res)
            let data = res["data"];

            for (let i = 0; i < data.length; i++) {
                let id = data[i]["id"]
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
                                  <td>
                                     <button onclick="questionConfirm('${id}')" class="button is-info is-small">질문하기</button>
                                  </td>
                                </tr>`;
                $("#rankingList").append(temp);
            } // end-for
        },
    });
}

function questionConfirm(reviewerId) {

}