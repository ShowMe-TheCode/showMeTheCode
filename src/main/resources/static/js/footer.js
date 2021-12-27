$(document).ready(function () {
    let footer = `<footer class="footer">
        <div class="wrapper">
            <div class="footer_bottom">
                <div class="footer_bottom_left footer_info">
                    <div class="footer_info_top">
                        <a href="https://github.com/ShowMe-TheCode/showMeTheCode" target="_blank">깃허브 링크</a>
                    </div>
                    <div class="footer_info_bottom">
                        <div class="is-mobile info_label">
                            내일배움캠프 |
                            <span class="is-mobile info_caret"><i class="far fa-angle-up"></i><i
                                    class="far fa-angle-down"></i></span>
                        </div>
                        <div class="info-dropdown">
                            <span class="is-hidden-mobile">내일배움캠프 1회차 | </span>
                            <span>팔색조 | </span>
                            <span>
                <a href="https://github.com/ShowMe-TheCode/showMeTheCode" target="_blank">깃허브 링크</a>
              </span>
                        </div>
                        ©8Colors-Bird. ALL RIGHTS RESERVED
                    </div>
                </div>
                <div class="footer_bottom_right is-hidden-mobile">
                    <a href="https://post.naver.com/inflearn" target="_blank"><i class="text">N</i></a>
                    <a href="https://blog.naver.com/inflearn" target="_blank"><i class="text">B</i></a>

                    <a href="https://www.youtube.com/channel/UC0Y0T9JpgIBbyGDjvy9PbOg" target="_blank"><i
                            class="fab fa-youtube"></i></a>
                </div>
            </div>
        </div>
    </footer>`
    $('#root').append(footer);
});