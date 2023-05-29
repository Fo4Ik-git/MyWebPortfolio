var editor = getElementByID("editor");
var htmlOutput = getElementByID("html-output");
var mediaPrompt = getElementByID("mediaPrompt");
var mediaUrlInput = getElementByID("mediaUrl");

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

// Функция для открытия окна выбора изображения
function openMediaPrompt() {
  showElement(mediaPrompt);
}

// Функция для закрытия окна выбора изображения
function closeMediaPrompt() {
  hideElement(mediaPrompt);
  mediaUrlInput.value = "";
}

// Функция для вставки видео из URL
function insertMedia() {
  var videoUrl = mediaUrlInput.value;
  var videoElement = document.createElement("div");
  videoElement.classList.add("embed-responsive");

  var videoWrapper = document.createElement("div");
  videoWrapper.classList.add("videoWrapper");

  var iframe = document.createElement("iframe");
  iframe.src = getVideoSource(videoUrl);
  iframe.classList.add("embed-responsive-item");
  iframe.allowFullscreen = true;
  iframe.contentEditable = true;
  iframe.frameBorder = 0;
  iframe.encriptedMedia = true;

  videoWrapper.appendChild(iframe);
  videoElement.appendChild(videoWrapper);

  editor.appendChild(videoElement);

  var br = document.createElement("br");
  br.classList.add("videoBreak");
  editor.appendChild(br);

  closeMediaPrompt();
  updateHtmlOutput();
}

// Функция для получения источника видео в зависимости от платформы
function getVideoSource(videoUrl) {
  if (videoUrl.includes("youtube.com")) {
    // YouTube video
    return videoUrl.replace(
      "https://www.youtube.com/watch?v=",
      "https://www.youtube.com/embed/"
    );
  } else if (videoUrl.includes("youtu.be")) {
    return videoUrl.replace(
      "https://youtu.be/",
      "https://www.youtube.com/embed/"
    );
  } else {
    // Default source for other video platforms
    return videoUrl;
  }
}

function updateHtmlOutput() {
  htmlOutput.textContent = editor.innerHTML;
}

// Инициализация редактора
function initializeEditor() {
    var alignButtons = document.querySelectorAll(".text-side button");
  
    alignButtons.forEach(function (button) {
      button.addEventListener("click", function () {
        var align = button.getAttribute("data-align");
        alignCode(align);
      });
    });
  
    // Привязываем обработчик события click к кнопке вставки изображения
    var insertMediaButton = getElementByID("insert-media-button");
    insertMediaButton.addEventListener("click", openMediaPrompt);
  
    // Привязываем обработчик события input для элемента imageUrlInput
    mediaUrlInput.addEventListener("input", function () {
      var imageUrl = mediaUrlInput.value;
      if (imageUrl) {
        insertMedia();
      }
    });
  
    // Вызываем инициализацию редактора при загрузке страницы
    window.addEventListener("DOMContentLoaded", initializeEditor);
  }


// Привязываем обработчик события input для элемента videoUrlInput
mediaUrlInput.addEventListener("input", function () {
  var videoUrl = mediaUrlInput.value;
  if (videoUrl) {
    insertMedia(videoUrl, true);
  }
});
