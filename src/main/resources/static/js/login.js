function show_login_modal() {

    temp_html = `<div id="login_modal" class="modal "><div class="dimmed"></div>
  <article class="sign-in-modal">
    <span onclick="close_login_modal()" class="e-close header__close-button">
      <svg width="16px" xmlns="http://www.w3.org/2000/svg" height="12" viewBox="0 0 12 12"><path fill="#3E4042" fill-rule="evenodd" d="M.203.203c.27-.27.708-.27.979 0L6 5.02 10.818.203c.27-.27.709-.27.98 0 .27.27.27.708 0 .979L6.978 6l4.818 4.818c.27.27.27.709 0 .98-.27.27-.709.27-.979 0L6 6.978l-4.818 4.818c-.27.27-.709.27-.98 0-.27-.27-.27-.709 0-.979L5.022 6 .203 1.182c-.27-.27-.27-.709 0-.98z" clip-rule="evenodd"></path></svg>
    </span>
    <span class="header__logo">
      <img src="./images/header_logo.png" style=" margin: auto" />
    </span>
    <form class="sign-in-modal__form">
      <div class="form__input-block">
        <input placeholder="이메일 또는 아이디 입력" data-kv="email" id="signin-id" class="form__input form__input--email ac-input--large"> 
        
<div class="ac-input-with-item--large password-input form__input ">
  
  <input onkeypress="if(event.keyCode==13){signin();}" class="e-sign-in-input" value="" data-kv="password" type="password" id="signin-password" placeholder="비밀번호">
  <span class="e-toggle-password form__toggle-password form__toggle-password--hidden"><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16"><path fill="#212529" d="M10.333 8c0 1.289-1.044 2.333-2.333 2.333C6.71 10.333 5.667 9.29 5.667 8 5.667 6.711 6.71 5.667 8 5.667c1.289 0 2.333 1.044 2.333 2.333z"></path><path fill="#212529" fill-rule="evenodd" d="M8 2.333c-2.288 0-4.083 1.023-5.37 2.16C1.348 5.63.544 6.902.22 7.469.03 7.8.03 8.2.22 8.533c.323.566 1.127 1.838 2.41 2.973 1.287 1.138 3.082 2.16 5.37 2.16 2.288 0 4.083-1.022 5.37-2.16 1.283-1.135 2.087-2.407 2.41-2.973.19-.333.19-.733 0-1.065-.323-.567-1.127-1.839-2.41-2.974-1.287-1.138-3.082-2.16-5.37-2.16zm-6.912 5.63c.295-.516 1.035-1.685 2.205-2.72C4.461 4.21 6.03 3.333 8 3.333c1.97 0 3.54.877 4.707 1.91 1.17 1.035 1.91 2.204 2.205 2.72.008.015.01.028.01.037 0 .01-.002.022-.01.037-.295.516-1.035 1.685-2.205 2.72-1.168 1.033-2.737 1.91-4.707 1.91-1.97 0-3.54-.877-4.707-1.91-1.17-1.035-1.91-2.204-2.205-2.72-.008-.015-.01-.028-.01-.037 0-.01.002-.022.01-.037z" clip-rule="evenodd"></path></svg></span>
</div>

      </div>
      
    <button type="button" onclick="signin()" class="ac-button is-md is-solid is-primary form__button e-sign-in">로그인</button>
    </form>
    <p class="sign-in-modal__more-action">
      <span onclick="signup_page()" id="button_signup" class="e-to-sign-up more-action__text more-action__text--sign-up">회원가입</span>
    </p>
    <div class="sign-in-modal__social-sign-in">
      <hr class="social-sign-in__line">
      <span class="social-sign-in__title">간편 로그인</span>
      <div class="social__sign-in-buttons">
        
  <button class="social__button social__button--Kakao e-o-auth" data-provider="Kakao">
    <svg width="18px" xmlns="http://www.w3.org/2000/svg" height="17" viewBox="0 0 18 17"><g transform="translate(0.000000,17.000000) scale(0.100000,-0.100000)" stroke="none"><path fill="#212529" d="M38 154 c-15 -8 -30 -25 -34 -38 -6 -26 10 -66 27 -66 7 0 9 -10 5 -26 -7 -25 -6 -25 16 -10 12 9 31 16 41 16 29 0 75 28 82 50 10 31 -3 59 -35 75 -36 19 -67 18 -102 -1z"></path></g></svg>
  </button>
        
  <button onclick="google_login()" class="social__button social__button--Google e-o-auth" data-provider="Google">
    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="none" viewBox="0 0 18 18"><path fill="#4285F4" d="M17.785 9.169c0-.738-.06-1.276-.189-1.834h-8.42v3.328h4.942c-.1.828-.638 2.073-1.834 2.91l-.016.112 2.662 2.063.185.018c1.694-1.565 2.67-3.867 2.67-6.597z"></path><path fill="#34A853" d="M9.175 17.938c2.422 0 4.455-.797 5.94-2.172l-2.83-2.193c-.758.528-1.774.897-3.11.897-2.372 0-4.385-1.564-5.102-3.727l-.105.01-2.769 2.142-.036.1c1.475 2.93 4.504 4.943 8.012 4.943z"></path><path fill="#FBBC05" d="M4.073 10.743c-.19-.558-.3-1.156-.3-1.774 0-.618.11-1.216.29-1.774l-.005-.119L1.254 4.9l-.091.044C.555 6.159.206 7.524.206 8.969c0 1.445.349 2.81.957 4.026l2.91-2.252z"></path><path fill="#EB4335" d="M9.175 3.468c1.684 0 2.82.728 3.468 1.335l2.531-2.471C13.62.887 11.598 0 9.175 0 5.667 0 2.638 2.013 1.163 4.943l2.9 2.252c.727-2.162 2.74-3.727 5.112-3.727z"></path></svg>
  </button>
        
      </div>
    </div>
  </article></div>`

    $('body').append(temp_html);
}

function google_login(){
    location.href="/oauth2/authorization/google"
}

function close_login_modal() {
    $('#login_modal').remove();
}

function signup_page() {
    location.href = "/signup.html"
}