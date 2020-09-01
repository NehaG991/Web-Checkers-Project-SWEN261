<!DOCTYPE html>

<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
  <meta http-equiv="refresh" content="10">
  <title>Web Checkers | ${title}</title>
  <link rel="stylesheet" type="text/css" href="/css/style.css">
</head>

<body>
<div class="page">

  <h1>Web Checkers | ${title}</h1>

  <!-- Provide a navigation bar -->
  <#include "nav-bar.ftl" />

  <div class="body">

    <!-- Provide a message to the user, if supplied. -->
    <#include "message.ftl" />

    <div class="column">
    <!-- List of all active Players -->
      <form action="/game" method="get">

        <#if playerList??>
          <div id="Active Players" class="INFO">Active Players (click to invite): </div>
          <#list playerList as player>
            <button class="player" name="opponent" type="submit" value="${player.name}">${player.name}</button> <br>
          </#list>
        </#if>

      </form>
    </div>

    <div class="column">
      <!-- List of all active Games -->
      <form action="/spectator/game" method="get">

        <#if activeGameList??>
          <div id="Active Games" class="INFO">Active Checkers Games <br> (click to spectate): </div>
            <#list activeGameList as game>
                <button class="game" name="gameID" type="submit"
                        value="${game.gameID}">Game ${game.gameID}: ${game.redPlayer.name} vs. ${game.whitePlayer.name}
                </button> <br>
            </#list>
        </#if>

      </form>
    </div>

    <div class="column">
      <!-- List of all saved, ended Games -->
      <form action="/replay/game" method="get">

        <#if endedGameList??>
          <div id="Stored Games" class="INFO">Stored Games (click to replay):</div>
          <#list endedGameList as game>
            <button class="game" name="gameID" type="submit"
                    value="${game.gameID}">Game ${game.gameID}: ${game.redPlayer.name} vs. ${game.whitePlayer.name}
            </button> <br>
          </#list>
        </#if>
      </form>
    </div>

  </div>

</div>
</body>

</html>
