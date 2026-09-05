import http.server
import mimetypes
import os
import socketserver

PORT = 3000
DIRECTORY = "/app/applet/public"

mimetypes.add_type("application/vnd.android.package-archive", ".apk")
mimetypes.add_type("text/html; charset=utf-8", ".html")
mimetypes.add_type("text/css", ".css")
mimetypes.add_type("application/javascript", ".js")

class CyberStrikeHTTPHandler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=DIRECTORY, **kwargs)

    def end_headers(self):
        self.send_header("Access-Control-Allow-Origin", "*")
        self.send_header("Cache-Control", "no-cache, no-store, must-revalidate")
        super().end_headers()

if __name__ == "__main__":
    os.chdir(DIRECTORY)
    # Enable address reuse so restarts don't hit address already in use
    socketserver.TCPServer.allow_reuse_address = True
    with socketserver.TCPServer(("", PORT), CyberStrikeHTTPHandler) as httpd:
        print(f"CyberStrike HTTP Download Server running on port {PORT}, serving {DIRECTORY}", flush=True)
        httpd.serve_forever()
