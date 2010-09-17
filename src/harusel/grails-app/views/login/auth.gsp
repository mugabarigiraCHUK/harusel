<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
  <title>Login</title>
<p:css name='main'/>
</head>

<body>
<center>
  <div id="page" class="style_main">
    <div class="style_headerLogin">

      <div class="style_logo"></div>
      <div class="style_userBlockLogin">
        <div class="style_userBlockLoginContent">
          <div id='login'>
            <div class='inner'>

              <div class='fheader'>Please Login..</div>
              <form action='${postUrl}' method='POST' id='loginForm' class='cssform'>
                <div>
                  <label for='j_username'>Login ID</label>
                  <input type='text' class='text_' name='j_username' id='j_username' value='${request.remoteUser}'/>
                </div>
                <div>
                  <label for='j_password'>Password</label>
                  <input type='password' class='text_' name='j_password' id='j_password'/>
                </div>
                <div>
                  <input type='submit' value='Login' id='loginButton'/>
                </div>
              </form>
            </div>
          </div>
          <script type='text/javascript'>
            <!--
            (function() {
              document.forms['loginForm'].elements['j_username'].focus();
            })();
            // -->
          </script>
        </div>
      </div>

    </div>


    <g:if test='${flash.message}'>
      <div class="errors">
        <div class="error-icon"></div>
        ${flash.message}
      </div>
    </g:if>
  </div>
</body>
</body>
</html>
