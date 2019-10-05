window.onload = function() {
  var xhr=new XMLHttpRequest();
  xhr.open("GET","/js/shutdownNow.js" + window.location.search);
  xhr.send();
};