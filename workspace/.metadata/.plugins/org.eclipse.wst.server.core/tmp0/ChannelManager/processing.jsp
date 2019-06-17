<%
	String processingGIF = "<img src=\"images/dp1.gif\" /><img style=\"padding: 20px; vertical-align: top;\" src=\"images/dp2.gif\" />";
	String resize = "";
	String version = request.getParameter("version");
	if(version!=null) {
		processingGIF = "<img id=\"pic\" src=\"images/" + version + ".gif\" />";
		resize = "onLoad=\"resize();\"";
	}
%>
<html>
  <head>
  	<title>Processing...</title>
  	<script>
  	function resize() {
  	  	var pic = document.getElementById("pic");
  	   	var height_ = pic.height;
  	   	var width_ = pic.width;
  	  	window.resizeTo(width_, height_);
  	}
  	</script>
  </head>
  <body style="margin: 0px; background-color: white;" <%= resize %>>
<%= processingGIF %>
  </body>
</html>

	 

