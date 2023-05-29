var editor = getElementByID("editor");
var htmlOutput = getElementByID("html-output");
var linkPrompt = getElementByID("linkPrompt");
var linkTextInput = getElementByID("linkText");
var linkUrlInput = getElementByID("linkUrl");

// Функция для получения элемента по его идентификатору
function getElementByID(id) {
  return document.getElementById(id);
}

// Функция для скрытия элемента
function hideElement(element) {
  element.style.display = "none";
}

// Функция для отображения элемента
function showElement(element) {
  element.style.display = "block";
}

// Функция для открытия окна вставки ссылки
function openLinkPrompt() {
  showElement(linkPrompt);
}

// Функция для закрытия окна вставки ссылки
function closeLinkPrompt() {
  hideElement(linkPrompt);
  linkTextInput.value = "";
  linkUrlInput.value = "";
}

// Функция для вставки ссылки
function insertLink() {
  var linkText = linkTextInput.value;
  var linkUrl = linkUrlInput.value;

  if (linkText && linkUrl) {
    var link = document.createElement("a");
    link.href = linkUrl;
    link.textContent = linkText;
    link.target = "_blank";

    editor.appendChild(link);
    updateHtmlOutput();

    closeLinkPrompt();
  }
}
