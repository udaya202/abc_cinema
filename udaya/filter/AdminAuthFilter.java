package filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/admin/*")
public class AdminAuthFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);
        
        boolean isLoggedIn = session != null && 
                            session.getAttribute("isLoggedIn") != null && 
                            (Boolean) session.getAttribute("isLoggedIn") && 
                            "admin".equals(session.getAttribute("role"));
        
        if (isLoggedIn) {
            chain.doFilter(request, response);
        } else {
            resp.sendRedirect("/adminlogin");
        }
    }
    
    @Override
    public void destroy() {
    }
} 