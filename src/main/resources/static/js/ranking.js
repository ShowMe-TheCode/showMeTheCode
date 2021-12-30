$(document).ready(function() {
    getRankingAll()
})


// ========================================
// 리뷰어 랭킹 - 전체 보기
// ========================================
function getRankingAll() {
    console.log("getRankingAll 호출")

    $("#rankingList").empty();

    $.ajax({
        type: "GET",
        url: base_url + "/reviewers/rank",
        success: function (res) {
            console.log(res)
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