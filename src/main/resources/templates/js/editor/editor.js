// Получаем ссылки на элементы
var editor = getElementByID("editor");
var htmlOutput = getElementByID("html-output");
var copyButton = getElementByID("copy-button");

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

// Функция для изменения стиля текста в редакторе
function changeTextStyle(style) {
  document.execCommand(style, false, null);
  updateHtmlOutput();
}

// Функция для изменения шрифта
function changeFont() {
  var fontSelect = getElementByID("font-select");
  var selectedFont = fontSelect.value;
  document.execCommand("fontName", false, selectedFont);
  updateHtmlOutput();
}

// Функция для изменения размера шрифта
function changeFontSize() {
  var fontSizeInput = getElementByID("font-size-input");
  var fontSize = fontSizeInput.value + "px";

  var selection = window.getSelection();
  if (selection.rangeCount > 0) {
    var range = selection.getRangeAt(0);
    var selectedText = range.extractContents();

    var span = document.createElement("span");
    span.style.fontSize = fontSize;
    span.appendChild(selectedText);

    range.insertNode(span);
  }
  updateHtmlOutput();
}

// Функция для изменения положения текста
function alignCode(align) {
  document.execCommand("justify" + align, false, null);
  updateHtmlOutput();
}

// Закрывать выпадающий список при клике вне его области
window.addEventListener("click", function (event) {
  var alignCodeDropdown = document.getElementById("align-code-dropdown");
  if (!alignCodeDropdown.contains(event.target)) {
    var dropdownMenu = document.querySelector(".dropdown-menu");
    if (dropdownMenu.classList.contains("show")) {
      dropdownMenu.classList.remove("show");
    }
  }
});

// Создание окна редактирования стиля и высоты текста
function createStyleEditor() {
  var styleEditor = document.createElement("div");
  styleEditor.id = "style-editor";
  styleEditor.className = "style-editor";

  styleEditor.style.position = "fixed";
  styleEditor.style.display = "none";
  styleEditor.style.backgroundColor = "white";
  styleEditor.style.padding = "10px";
  styleEditor.style.border = "1px solid black";

  var editor = getElementByID("editor");
  editor.appendChild(styleEditor);
}

// Обработчик события клика на редактор
function handleEditorClick(event) {
  updateStyleEditorPosition(event);
}

// Функция для копирования содержимого html-output
function copyHtmlOutput() {
  // Создаем временный элемент textarea для копирования текста
  var tempTextarea = document.createElement("textarea");
  tempTextarea.value = htmlOutput.textContent;
  document.body.appendChild(tempTextarea);

  // Выделяем текст в textarea и копируем его в буфер обмена
  tempTextarea.select();
  document.execCommand("copy");

  // Удаляем временный элемент textarea
  document.body.removeChild(tempTextarea);

  copyButton.innerHTML = '<i class="fa-solid fa-check"></i>';
}

// Инициализация редактора
function initializeEditor() {
  createStyleEditor();

  var editor = getElementByID("editor");
  editor.addEventListener("mousedown", handleEditorClick);
}

// Обновление позиции окна редактирования
function updateStyleEditorPosition(event) {
  var selection = window.getSelection();
  if (event.button === 2 && selection.toString().length > 0) {
    var styleEditor = getElementByID("style-editor");
    styleEditor.style.display = "block";
    styleEditor.style.top = event.pageY + 10 + "px";
    styleEditor.style.left = event.pageX + 10 + "px";
  } else {
    var styleEditor = getElementByID("style-editor");
    styleEditor.style.display = "none";
  }
}

// Вызываем инициализацию редактора при загрузке страницы
window.addEventListener("DOMContentLoaded", initializeEditor);

// Добавляем обработчик события input для элемента editor
editor.addEventListener("input", function () {
  var selection = window.getSelection();
  var range = selection.getRangeAt(0);
  var startContainer = range.startContainer;
  var endContainer = range.endContainer;
  copyButton.innerHTML = '<i class="fa-regular fa-clipboard"></i>';

  // Проверяем, являются ли начальный и конечный контейнеры родителями editor
  if (editor.contains(startContainer) && editor.contains(endContainer)) {
    // Обновляем содержимое html-output
    updateHtmlOutput();
  }
});

// Функция для обновления содержимого html-output
function updateHtmlOutput() {
  htmlOutput.textContent = editor.innerHTML;
}

// Вызываем функцию updateHtmlOutput при загрузке страницы
updateHtmlOutput();
