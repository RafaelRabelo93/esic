var input = document.getElementsByClassName("Input");
input.addEventListener("keyup", function(event) {
    event.preventDefault();
    if (event.keyCode === 13) {
        document.getElementsByClassName("PressKey").click();
    }
});