/* parse workbook */
const params = new URLSearchParams(window.location.search);
const fileUrl = params.get("file");
var xhr = new XMLHttpRequest
xhr.onload = function () {
  const workbook = XLSX.read(xhr.response);
  var sheetName = workbook.SheetNames
  var divHtml = "";
  sheetName.forEach(function(item,index,arr){
    var sheet = workbook.Sheets[sheetName[index]]
    divHtml += "<p class=\"sheetName\">" + item + "</p>"
    divHtml += XLSX.utils.sheet_to_html(sheet);
  });

  const content = document.getElementById("excelPreview");
  content.innerHTML = divHtml;
}
xhr.onerror = function () {
  reject(new TypeError('Local request failed'))
}
xhr.open('GET', fileUrl, true)
xhr.responseType = "arraybuffer";
xhr.send(null)
