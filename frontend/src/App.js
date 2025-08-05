import Load from "./mainpage/load.js";
import Main from "./mainpage/main.js";
import Login from "./login/login.js";
import Register from "./login/register.js";
import Forgot from "./login/forgot.js";
import Newpw from "./login/newpw.js";
import Select from "./coaching/select.js";
import Coach from "./coaching/coach.js";
import UploadFile from "./mainpage/uploadFile.js";

import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";

function App() {
  return (
    <Router>
      <div className="App">
        <Routes>
          {/* 기본 경로 - 로딩 페이지 */}
          <Route path="/" element={<Load />} />

          {/* 인증 관련 라우트 */}
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/forgot" element={<Forgot />} />
          <Route path="/reset-password" element={<Newpw />} />

          {/* 메인 기능 라우트 */}
          <Route path="/upload" element={<UploadFile />} />
          <Route path="/main" element={<Main />} />

          {/* 코칭 기능 라우트 */}
          <Route path="/coaching" element={<Select />} />
          <Route path="/coaching/select" element={<Select />} />
          <Route path="/coaching/result" element={<Coach />} />

          {/* 잘못된 경로 처리 - 기본 페이지로 리다이렉트 */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
