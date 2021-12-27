$(document).ready(function () {
	if (localStorage.getItem("userId") != null) {
		let id = localStorage.getItem("userId");
		connectSSE(id);
	}

	$("#signup-isReviewer").change(function () {
		if ($("#signup-isReviewer").is(":checked")) {
			$("#language-check-box").show();
		} else {
			$("#language-check-box").hide();
		}
	});

	$("#signup-id").keyup(function () {
		var email = $("#signup-id").val();

		if (email != 0) {
			if (isValidEmailAddress(email)) {
				$("#error-email").addClass("form__error--hide");
			} else {
				$("#error-email").text("이메일 형식이 올바르지 않습니다.");
				$("#error-email").removeClass("form__error--hide");
			}
		} else {
			console.log(3);
		}
	});

	$("#emailConfirm").keyup(function () {
		let email = $("#signup-id").val();
		let email2 = $("#emailConfirm").val();

		if (email != email2) {
			$("#error-email-check").text("이메일이 일치하지 않습니다.");
			$("#error-email-check").removeClass("form__error--hide");
		} else {
			$("#error-email-check").addClass("form__error--hide");
		}
	});
});

// 로그인
function signin() {
	// SSE
	const id = document.getElementById("signin-id").value;

	let username = $("#signin-id").val();
	let password = $("#signin-password").val();

	let data = {
		username: username,
		password: password,
	};

	$.ajax({
		type: "POST",
		url: base_url + "/user/signin",
		contentType: "application/json;charset=utf-8;",
		data: JSON.stringify(data),
		success: function (res) {
			console.log(res);
			let id = res["id"];

			localStorage.setItem("mytoken", res["token"]);
			localStorage.setItem("myAuthority", res["authority"]);
			localStorage.setItem("userId", id);

			alert("로그인에 성공했습니다.");
			location.href = "index.html";
		},
		error: function (err) {
			alert("로그인에 실패했습니다.");
		},
	});
}

// SSE 연결
function connectSSE(key) {
	let subscribeUrl = base_url + `/subscribe/${key}`;
	const eventSource = new EventSource(subscribeUrl);
	// TODO access-token 헤더에 넣어야지 작동
	eventSource.addEventListener("sse", function (event) {
		console.log(event.data);
		const data = JSON.parse(event.data);
		console.log(data.content);
		console.log(data);
		if (data.content) {
			alert(data.content);
		}
		// (async () => {
		//  // create and show the notification
		//  const showNotification = () => {
		//      // create a new notification
		//      const notification = new Notification("ShowMeTheCode", {
		//          body: data.content
		//      });
		//      // close the notification after 10 seconds
		//      setTimeout(() => {
		//          notification.close();
		//      }, 10 * 1000);
		//      // navigate to a URL when clicked
		//      notification.addEventListener("click", () => {
		//          window.open(data.url, "_blank");
		//      });
		//  };
		//  // show an error message
		//  const showError = () => {
		//      const error = document.querySelector(".error");
		//      error.style.display = "block";
		//      error.textContent = "You blocked the notifications";
		//  };
		//  // check notification permission
		//  let granted = false;
		//  if (Notification.permission === "granted") {
		//      granted = true;
		//  } else if (Notification.permission !== "denied") {
		//      let permission = await Notification.requestPermission();
		//      granted = permission === "granted" ? true : false;
		//  }
		//  // show notification or error
		//  granted ? showNotification() : showError();
		//  showNotification();
		// }
		// )
		// ();
	});
}

// 회원가입
function signup() {
	if (!isValidEmailAddress($("#signup-id").val())) {
		return alert("이메일을 확인해주세요.");
	}

	let id = $("#signup-id").val();
	let nickname = $("#signup-nickname").val();
	let password = $("#signup-password").val();
	let isReviewer = $('input[id="signup-isReviewer"]').is(":checked");
	let data = {};
	let langs = [];
	if (isReviewer) {
		langs.push($("select[name=language]").val());
	}

	data = {
		username: id,
		nickname: nickname,
		password: password,
		reviewer: isReviewer,
		languages: langs,
	};

	$.ajax({
		type: "POST",
		url: base_url + "/user/signup",
		contentType: "application/json;charset=utf-8;",
		data: JSON.stringify(data),
		success: function (res) {
			alert("회원가입에 성공했습니다.");
			location.href = "index.html";
		},
		error: function (err) {
			// console.log(err)
			// console.log(err.responseJSON.message)
			console.log(err);
			alert("회원가입에 실패했습니다.");
		},
	});
}

function logout() {
	$.ajax({
		type: "POST",
		url: base_url + "/user/logout",
		success: function (res) {
			if (res["result"] == "success") {
				localStorage.clear();
				alert(res["message"]);
				window.location.reload();
			}
		},
	});
}

function openSigninModal() {
	$("#signin-id").val("");
	$("#signin-password").val("");
	$("#signinModal").modal("show");
}

function openSignupModal() {
	$("#signup-id").val("");
	$("#signup-password").val("");
	$("#signupModal").modal("show");
}

function isValidEmailAddress(emailAddress) {
	var pattern = new RegExp(
		/^(("[\w-\s]+")|([\w-]+(?:\.[\w-]+)*)|("[\w-\s]+")([\w-]+(?:\.[\w-]+)*))(@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$)|(@\[?((25[0-5]\.|2[0-4][0-9]\.|1[0-9]{2}\.|[0-9]{1,2}\.))((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\.){2}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\]?$)/i
	);
	return pattern.test(emailAddress);
}

function isValidPassword(password) {
	var pattern = new RegExp(
		/^(("[\w-\s]+")|([\w-]+(?:\.[\w-]+)*)|("[\w-\s]+")([\w-]+(?:\.[\w-]+)*))(@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$)|(@\[?((25[0-5]\.|2[0-4][0-9]\.|1[0-9]{2}\.|[0-9]{1,2}\.))((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\.){2}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\]?$)/i
	);
	return pattern.test(password);
}
