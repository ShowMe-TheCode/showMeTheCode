$(document).ready(function () {
	let navbar = `<!--    모바일 하위 Nav바 -->
    <header id="header">
        <nav class="navbar">

            <div class="container mobile_container">
                <div class="content">
                    <div class="mobile_left">
                    <span class="infd-icon e_left_aside_btn" data-type="open">
<!--                    <svg width="30" height="30" width="24" height="24" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path fill="#495057" fill-rule="evenodd" clip-rule="evenodd" d="M1.5 6C1.22386 6 1 6.22386 1 6.5C1 6.77614 1.22386 7 1.5 7H10C10.2761 7 10.5 6.77614 10.5 6.5C10.5 6.22386 10.2761 6 10 6H1.5ZM1.5 16C1.22386 16 1 16.2239 1 16.5C1 16.7761 1.22386 17 1.5 17H10C10.2761 17 10.5 16.7761 10.5 16.5C10.5 16.2239 10.2761 16 10 16H1.5ZM1 11.5C1 11.2239 1.22386 11 1.5 11H8.5C8.77614 11 9 11.2239 9 11.5C9 11.7761 8.77614 12 8.5 12H1.5C1.22386 12 1 11.7761 1 11.5ZM15.5 15.5C17.7091 15.5 19.5 13.7091 19.5 11.5C19.5 9.29086 17.7091 7.5 15.5 7.5C13.2909 7.5 11.5 9.29086 11.5 11.5C11.5 13.7091 13.2909 15.5 15.5 15.5ZM15.5 16.5C16.7006 16.5 17.8024 16.0768 18.6644 15.3715L22.1464 18.8536C22.3417 19.0488 22.6583 19.0488 22.8536 18.8536C23.0488 18.6583 23.0488 18.3417 22.8536 18.1464L19.3715 14.6644C20.0768 13.8024 20.5 12.7006 20.5 11.5C20.5 8.73858 18.2614 6.5 15.5 6.5C12.7386 6.5 10.5 8.73858 10.5 11.5C10.5 14.2614 12.7386 16.5 15.5 16.5Z"/></svg></span>-->
                    </div>
                    <div class="brand_header">
                        <a href="/" class="brand_logo"><span class="visually_hidden">쇼미 더 코드</span>
                            <img src="images/logo.png">
                        </a>
                    </div>
                    <div class="mobile_right">

                        <a role="button" onclick="show_login_modal()"
                           class="button space-inset-4 text is-5 signin" id="signinBtnMobile">로그인</a>
                        <a role="button" class="button space-inset-4 text is-5 signin" onclick="logout()" id="logoutBtnMobile">로그아웃</a>
                        <a href="signup.html" class="e-signup button space-inset-4 text is-5 is-primary " id="signupBtnMobile">회원가입</a>
                        <a href="mypage.html" role="button" class="button space-inset-4 text is-5" id="myPageBtnMobile">마이페이지</a>
                        </div>
                </div>

            </div>


            <div class="container desktop_container">
                <div class="content">
                    <div class="brand_header">
                        <a href="/" class="brand_logo"> <span class="visually_hidden">쇼미 더 코드</span>
                            <img src="images/logo.png">
                        </a>
                    </div>
                    <!--상단 Nav바 펼쳐지는 부분-->
                    <div class="navbar-menu">
                        <div class="navbar-left">
<!--                            <div class="has-dropdown is-hoverable navbar-item  icon_drop_menu">-->
<!--                                <a href="/community/questions" class="navbar-item "><span>커뮤니티</span></a>-->
<!--                                <div class="navbar-dropdown is-boxed is-right">-->

<!--                                    <a class="navbar-item " href="/community/questions">-->
<!--                                        <span class="icon"><i class="fal fa-comment-alt-edit"></i></span> <span-->
<!--                                            class="name">질문 & 답변</span>-->

<!--                                    </a>-->

<!--                                    <a class="navbar-item " href="/community/chats">-->
<!--                                        <span class="icon"><i class="fal fa-comment-alt-dots"></i></span> <span-->
<!--                                            class="name">자유주제</span>-->

<!--                                    </a>-->

<!--                                    <a class="navbar-item " href="/community/studies">-->
<!--                                        <span class="icon"><i class="fal fa-book-open"></i></span> <span class="name">스터디</span>-->

<!--                                    </a>-->

<!--                                    <a class="navbar-item " href="/blogs">-->
<!--                                        <span class="icon"><i class="fal fa-feather-alt"></i></span> <span class="name">블로그</span>-->

<!--                                    </a>-->

<!--                                </div>-->
<!--                                </a>-->
<!--                            </div>-->


<!--                            <div class="has-dropdown is-hoverable navbar-item  icon_drop_menu">-->
<!--                                <a href="/inflearn" class="navbar-item "><span>쇼미 더 코드</span></a>-->
<!--                                <div class="navbar-dropdown is-boxed is-right">-->

<!--                                    <a class="navbar-item " href="/community/reviews">-->
<!--                                        <span class="icon"><i class="fal fa-star"></i></span> <span-->
<!--                                            class="name">수강평</span>-->

<!--                                    </a>-->
<!--                                </div>-->
<!--                                </a>-->
<!--                            </div>-->

                        </div>
                        <div class="navbar-right">

                            <div class="search search_bar navbar-item header_search">
                                <input type="text" class="input" placeholder="" data-kv="headerSearchWord">
                                <span class="search__icon e-header-search"><i class="far fa-search"></i></span>
                            </div>

                            <div class="navbar-item buttons">

                                <a onclick="show_login_modal()" role="button"
                                   class="button space-inset-4 signin" id="signinBtn">로그인</a>
                                <a role="button" class="button space-inset-4 text is-5 signin" onclick="logout()" id="logoutBtn">로그아웃</a>
                                <a href="signup.html" class="e-signup button space-inset-4 is-primary" id="signupBtn">회원가입</a>
                                <a href="mypage.html" class="e-signup button space-inset-4 is-primary" id="mypageBtn">마이페이지</a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </nav>
    </header>`;
	$("#root").prepend(navbar);
});
