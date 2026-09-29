const FtpSrv = require('ftp-srv');

const server = new FtpSrv({
  url: "ftp://127.0.0.1:2121",
  pasv_url: "127.0.0.1",
  pasv_min: 2130,
  pasv_max: 2140
});

server.on('login', ({ username, password }, resolve, reject) => {
  if (username === "cms_test" && password === "senhaTest123") {
    resolve({ root: "./fixtures" });
  } else {
    reject(new Error("Invalid credentials"));
  }
});

server.listen().then(() => {
  console.log("FTP server running at ftp://127.0.0.1:2121");
});
