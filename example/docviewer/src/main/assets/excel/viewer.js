/* parse workbook */
//const url = "https://sheetjs.com/data/PortfolioSummary.xls";
//const url = "file:///android_asset/sample3.xlsx";
const url = "https://www.qycloud.com.cn/api/download/forKey?host=aliyun&fileName=sample3.xlsx&filePath=qFile%2FqChat%2Fim000000117731703130152909_157_sample3.xlsx&expires=-1&iosType=qycloudwebchat&entId=qFile&sign=MTcwMzEzMDE1MzpVbzQ5RENuZHZVMk9ESnJmaFprVDg2VjVNN2szNnM5elAyV09leFRJK0R2U3gzMk92dUNsOEI0YnozYzVxNUUwRkU0enJpekpMSDdFeFNHdHo4UW9oUT09OmhzcThDYTZXTm16MmF4cXJtQjVVSElGbU1ENXg2Yk5aMHNYZHoxTFRjeTQ0eDVMVlBrMmt5QUtFdUNTSlV3K05qUzdxbnpyRTRobWFGdTVIM2pFRTNnPT0=&styles=eyJmaWxlSU9Eb21haW4iOiJodHRwczovL3d3dy5xeWNsb3VkLmNvbS5jbiJ9&isView=0";

var xhr = new XMLHttpRequest
xhr.onload = function () {
  console.log("======", xhr.response)
  const workbook = XLSX.read(xhr.response);
  console.log("======", workbook)
  var sheetName = workbook.SheetNames
  var sheet = workbook.Sheets[sheetName]
  const content = document.getElementById("excelPreview");
  content.innerHTML = XLSX.utils.sheet_to_html(sheet);
}
xhr.onerror = function () {
  reject(new TypeError('Local request failed'))
}
xhr.open('GET', url, true)
xhr.responseType = "arraybuffer";
xhr.send(null)