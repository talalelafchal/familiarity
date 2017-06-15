package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class index_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List<String> _jspx_dependants;

  private org.glassfish.jsp.api.ResourceInjector _jspx_resourceInjector;

  public java.util.List<String> getDependants() {
    return _jspx_dependants;
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;

    try {
      response.setContentType("text/html");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;
      _jspx_resourceInjector = (org.glassfish.jsp.api.ResourceInjector) application.getAttribute("com.sun.appserv.jsp.resource.injector");

      out.write("<html>\r\n");
      out.write("<head>\r\n");
      out.write("    <meta http-equiv=\"content-type\" content=\"text/html;charset=utf-8\">\r\n");
      out.write("    <meta  charset=\"utf-8\" />\r\n");
      out.write("    <link rel=\"stylesheet\" href=\"lib/css/bootstrap.css\">\r\n");
      out.write("    <link rel=\"stylesheet\" href=\"lib/css/flat-ui.css\">\r\n");
      out.write("    <script type=\"text/javascript\" src=\"lib/js/bootstrap.js\"></script>\r\n");
      out.write("    <script type=\"text/javascript\" src=\"lib/js/sql.js\"></script>\r\n");
      out.write("    <script type=\"text/javascript\" src=\"index.js\"></script>\r\n");
      out.write("</head>\r\n");
      out.write("<body style=\"background-color: #1abc9c;\">\r\n");
      out.write("    <div style=\"margin-top: 15%\" class=\"container-fluid\">\r\n");
      out.write("        <div style=\"\" class=\"row\">\r\n");
      out.write("            <div class=\"col-md-4\"></div>\r\n");
      out.write("            <div class=\"col-md-1\">\r\n");
      out.write("                <div style=\"position: static\" class=\"login-icon\">\r\n");
      out.write("                    <img src=\"lib/img/bee.png\" alt=\"Welcome to Mail App\" />\r\n");
      out.write("                    <h4>Welcome to <small>Hive Web</small></h4>\r\n");
      out.write("                </div>\r\n");
      out.write("            </div>\r\n");
      out.write("            <div class=\"col-md-2\">\r\n");
      out.write("                <div class=\"login-form\">\r\n");
      out.write("                    <div class=\"form-group\">\r\n");
      out.write("                        <input type=\"text\" class=\"form-control login-field\" value=\"\" placeholder=\"Enter your account\" id=\"login-name\" />\r\n");
      out.write("                        <label class=\"login-field-icon fui-user\" for=\"login-name\"></label>\r\n");
      out.write("                    </div>\r\n");
      out.write("\r\n");
      out.write("                    <div class=\"form-group\">\r\n");
      out.write("                        <input type=\"password\" class=\"form-control login-field\" value=\"\" placeholder=\"Password\" id=\"login-pass\" />\r\n");
      out.write("                        <label class=\"login-field-icon fui-lock\" for=\"login-pass\"></label>\r\n");
      out.write("                    </div>\r\n");
      out.write("\r\n");
      out.write("                    <a class=\"btn btn-primary btn-lg btn-block\" href=\"#\">Log in</a>\r\n");
      out.write("                    <a class=\"login-link\" href=\"#\">Reset Password</a>\r\n");
      out.write("                </div>\r\n");
      out.write("            </div>\r\n");
      out.write("            <div class=\"col-md-5\"></div>\r\n");
      out.write("        </div>\r\n");
      out.write("    </div>\r\n");
      out.write("\r\n");
      out.write("</body>\r\n");
      out.write("</html>\r\n");
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          out.clearBuffer();
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
        else throw new ServletException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
