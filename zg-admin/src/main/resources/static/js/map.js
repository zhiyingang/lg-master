/*BaiDu MAP API*/
function MP(ak) {
    var script = document.createElement("script");
    script.type = "text/javascript";
    script.src = "http://api.map.baidu.com/api?v=2.0&ak="+ak+"&callback=init";
    document.head.appendChild(script);
}
