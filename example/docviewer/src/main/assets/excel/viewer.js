(async() => {
  /* parse workbook */
  const url = "https://sheetjs.com/data/PortfolioSummary.xls";
//  const url = "https://www.qycloud.com.cn/api/download/forKey?host=aliyun&fileName=%E7%AD%9B%E9%80%89JS%E4%BA%8B%E4%BB%B6.xlsx&filePath=qFile%2FqChat%2Fim000000117731703052897469_564_%E7%AD%9B%E9%80%89JS%E4%BA%8B%E4%BB%B6.xlsx&expires=-1&iosType=qycloudwebchat&entId=qFile&sign=MTcwMzA1Mjg5ODpZVGh2Ty9DZnpGMmJ2ZVFYb1orZU03SjJ5R0l1Z3FjVWVHdGMrZ3NhcGRzclpVVE1KSkMrcmR5dVhJaGN1cnBIbGJGMFBLQzJLejV5bEtsdDJTVUw3QT09Ok9EcWVsb0lmMUFqclJNOHdqTE83QmRUbnp3aHhaaWxDRzJieDg4Nk1ST2ZwRXlkUTJXYVgzaWN3TVA4UkxtemtsRU5yc0Jjc3RRenZEVTloSVhVNEFBPT0=&styles=eyJmaWxlSU9Eb21haW4iOiJodHRwczovL3d3dy5xeWNsb3VkLmNvbS5jbiJ9&isView=0";
//  const url = "https://www.qycloud.com.cn/api/download/forKey?host=aliyun&fileName=%E5%90%AF%E4%B8%9A%E4%BA%912023%E5%B9%B4%E5%BA%A6%E5%A5%97%E9%A4%90%E6%96%B9%E6%A1%88%EF%BC%88%E9%80%9A%E7%9F%A5%EF%BC%89.xlsx&filePath=qFile%2FqChat%2Fim000000117731703056784089_301_%E5%90%AF%E4%B8%9A%E4%BA%912023%E5%B9%B4%E5%BA%A6%E5%A5%97%E9%A4%90%E6%96%B9%E6%A1%88%EF%BC%88%E9%80%9A%E7%9F%A5%EF%BC%89.xlsx&expires=-1&iosType=qycloudwebchat&entId=qFile&sign=MTcwMzA1Njc4NDp3aVd3bHVJSVBLdTBvdWlNckM1VmRJOUJUTEdjMExXQVg1WWJVaFBGSjFYWWkvTFZKYzFRL0FMbXZDbGNLZEt3d0tKTk45cU5JL2JjVURod1ZGdzU2dz09OnlJWkZSbDRiZ2hSSFNYTHNHVENUVFBaeHdIWDdRNTh3T29xbUVGeUhSMXlKOXl0ME9MNElUeVovNXIvb3orYWdubUM1VzhyZzFKWnJVRkpsZEEraC9BPT0=&styles=eyJmaWxlSU9Eb21haW4iOiJodHRwczovL3d3dy5xeWNsb3VkLmNvbS5jbiJ9&isView=0";
  const workbook = XLSX.read(await (await fetch(url)).arrayBuffer());
//  const url = "file:///android_asset/sample3.xlsx";
//  const workbook = XLSX.read(await (await File(url)).arrayBuffer());

  var sheetName = workbook.SheetNames
  var sheet = workbook.Sheets[sheetName]
  const content = document.getElementById("excelPreview");
  content.innerHTML = XLSX.utils.sheet_to_html(sheet);
  console.log("====", content.innerHTML);

})();