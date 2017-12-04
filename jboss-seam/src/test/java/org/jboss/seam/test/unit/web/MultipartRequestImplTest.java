package org.jboss.seam.test.unit.web;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Map;

import org.jboss.seam.mock.EnhancedMockHttpServletRequest;
import org.jboss.seam.web.MultipartRequestImpl;
import org.testng.annotations.Test;

public class MultipartRequestImplTest
{
   private static final String CRLF = "\r\n";
   private static final String HYPHENS = "--";

   @Test
   public void testParseRequestBasic() throws Throwable
   {
      String boundary = "boundary10"; //10 bytes

      String data =
         HYPHENS + boundary + CRLF +
         "Content-Disposition: form-data; name=\"foo\"" + CRLF +
         CRLF +
         "bar" + CRLF +
         HYPHENS + boundary + HYPHENS;
          
      byte[] dataBytes = data.getBytes("UTF-8");

      EnhancedMockHttpServletRequest req = new EnhancedMockHttpServletRequest();
      req.setContent(dataBytes);
      req.setContentType("multipart/form-data; boundary=" + boundary);
      MultipartRequestImpl r = new MultipartRequestImpl(req, false, 0);
      Map m = r.getParameterMap();
      assertNotNull(m);
      assertEquals(r.getParameterValues("foo")[0], "bar");
   }

   @Test
   public void testParseRequestBufferBoundary() throws Throwable
   {

      int bufferSize = 2048; // See MultipartRequestImpl

      String boundary = "boundary10";

      String paddingParameter =
         HYPHENS + boundary + CRLF +
         "Content-Disposition: form-data; name=\"padding\"" + CRLF +
         CRLF;

      String testParameter =
         HYPHENS + boundary + CRLF +
         "Content-Disposition: form-data; name=\"foo\"" + CRLF +
         CRLF +
         "bar" + CRLF +
         HYPHENS + boundary + HYPHENS;

      // let's put test parameter near the buffer boundary, from (bufferSize - 100) to (bufferSize + 100)
      for (int i = -100; i < 100; i++)
      {
         StringBuffer buffer = new StringBuffer(bufferSize + 256);
         buffer.append(paddingParameter);
         int paddingSize = bufferSize - i - paddingParameter.length() - CRLF.length();
         appendPaddingValue(buffer, paddingSize);
         buffer.append(CRLF);
         buffer.append(testParameter);
         String data = buffer.toString();

         byte[] dataBytes = data.getBytes("UTF-8");
         EnhancedMockHttpServletRequest req = new EnhancedMockHttpServletRequest();
         req.setContent(dataBytes);
         req.setContentType("multipart/form-data; boundary=" + boundary);
         MultipartRequestImpl r = new MultipartRequestImpl(req, false, 0);
         Map m = r.getParameterMap();
         assertNotNull(m);
         assertEquals(r.getParameterValues("foo")[0], "bar");
      }
   }

   private static StringBuffer appendPaddingValue(StringBuffer buffer, int length)
   {
      for (int i = 0; i < length; i++)
      {
         buffer.append("x");
      }
      return buffer;
   }
}
