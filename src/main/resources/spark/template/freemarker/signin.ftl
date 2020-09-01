<!DOCTYPE html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
    <title>Web Checkers | ${formType}</title>
    <link rel="stylesheet" type="text/css" href="/css/style.css">
</head>

<div class="page">

    <h1> Web Checkers | ${formType}</h1>

    <div class="body">
        <form id="signin" action="./signin" method="POST">
            <input name="myUsername"
               pattern="[a-zA-Z]+[a-zA-Z0-9 ]*"
               placeholder="Username"
               title="Your username can only contain alphanumeric values and spaces. You must have at least one letter in your username."
               required/>
            <button type="submit">Sign-in</button>
        </form>
    </div>
</div>