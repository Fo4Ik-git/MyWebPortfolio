// Получаем ссылки на элементы
var editor = getElementByID("editor");
var htmlOutput = getElementByID("html-output");
var imagePrompt = getElementByID("imagePrompt");
var imageUrlInput = getElementByID("imageUrl");

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

// Функция для вставки изображения
function insertImage() {
  var imageUrl = imageUrlInput.value;
  if (imageUrl) {
    var image = document.createElement("img");
    image.src = imageUrl;
    image.addEventListener("mousedown", handleImageMousedown);
    editor.appendChild(image);
    updateHtmlOutput();
    closeImagePrompt();
  }
}

// Функция для открытия окна выбора изображения
function openImagePrompt() {
  showElement(imagePrompt);
}

// Функция для закрытия окна выбора изображения
function closeImagePrompt() {
  hideElement(imagePrompt);
  imageUrlInput.value = "";
}

// Обработчик события mousedown для изображений
function handleImageMousedown(event) {
  event.preventDefault();
  var clickedImage = event.target;

  if (clickedImage.classList.contains("selected-image")) {
    // Если нажатие происходит на уже выделенном изображении, запоминаем стартовые координаты изменения размера
    resizeStartX = event.clientX;
    resizeStartY = event.clientY;
    document.addEventListener("mousemove", handleImageMousemove);
    document.addEventListener("mouseup", handleImageMouseup);
    return;
  }

  selectedImage = clickedImage;
  selectImage(selectedImage);
}

// Обработчик события mousemove для изображений
function handleImageMousemove(event) {
  event.preventDefault();

  var deltaX = event.clientX - resizeStartX;
  var deltaY = event.clientY - resizeStartY;

  // Определяем знаки изменения для горизонтальной и вертикальной осей
  var signX = Math.sign(deltaX);
  var signY = Math.sign(deltaY);

  // Определяем абсолютные значения изменения для горизонтальной и вертикальной осей
  var absDeltaX = Math.abs(deltaX);
  var absDeltaY = Math.abs(deltaY);

  // Проверяем, движется ли мышь по диагонали, начиная с левого верхнего угла
  if (absDeltaX > absDeltaY) {
    // Изменяем размеры изображения только по горизонтали
    var newWidth = selectedImage.offsetWidth + signX * absDeltaX;
    var ratio = selectedImage.offsetWidth / selectedImage.offsetHeight;
    var newHeight = newWidth / ratio;
    selectedImage.style.width = newWidth + "px";
    selectedImage.style.height = newHeight + "px";
  } else {
    // Изменяем размеры изображения только по вертикали
    var newHeight = selectedImage.offsetHeight + signY * absDeltaY;
    var ratio = selectedImage.offsetWidth / selectedImage.offsetHeight;
    var newWidth = newHeight * ratio;
    selectedImage.style.width = newWidth + "px";
    selectedImage.style.height = newHeight + "px";
  }

  resizeStartX = event.clientX;
  resizeStartY = event.clientY;
  updateHtmlOutput();
}

// Обработчик события mouseup для изображений
function handleImageMouseup(event) {
  event.preventDefault();
  document.removeEventListener("mousemove", handleImageMousemove);
  document.removeEventListener("mouseup", handleImageMouseup);
}

// Функция для выделения изображения
function selectImage(image) {
  var selectedImage = document.querySelector(".selected-image");
  if (selectedImage) {
    selectedImage.classList.remove("selected-image");
  }
  if (selectedImage !== image) {
    image.classList.add("selected-image");
  } else {
    selectedImage = null;
  }
}

// Функция для обновления содержимого html-output
function updateHtmlOutput() {
  htmlOutput.textContent = editor.innerHTML;
}

// Вызываем функцию updateHtmlOutput при загрузке страницы
updateHtmlOutput();

// Инициализация редактора
function initializeEditor() {
  var editor = getElementByID("editor");
  var alignButtons = document.querySelectorAll(".text-side button");

  alignButtons.forEach(function (button) {
    button.addEventListener("click", function () {
      var align = button.getAttribute("data-align");
      alignCode(align);
    });
  });

  // Привязываем обработчик события click к кнопке вставки изображения
  var insertImageButton = getElementByID("insert-image-button");
  insertImageButton.addEventListener("click", openImagePrompt);

  // Привязываем обработчик события input для элемента imageUrlInput
  imageUrlInput.addEventListener("input", function () {
    var imageUrl = imageUrlInput.value;
    if (imageUrl) {
      insertImage();
    }
  });

  // Вызываем инициализацию редактора при загрузке страницы
  window.addEventListener("DOMContentLoaded", initializeEditor);
}

// Обработчик события click для всего документа
document.addEventListener("click", function (event) {
  var target = event.target;
  var isImage = target.tagName === "IMG" || target.parentElement.tagName === "IMG";
  var selectedImage = document.querySelector(".selected-image");

  // Если цель события не является изображением или его родительским элементом, снимаем выделение
  if (!isImage && selectedImage) {
    selectedImage.classList.remove("selected-image");
  }
});
