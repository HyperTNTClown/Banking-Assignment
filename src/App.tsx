import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'
import {BrowserRouter, Route, Routes } from "react-router-dom";
import Home from "./pages/home.tsx";
import About from "./pages/about.tsx";
import Login from "./pages/login.tsx";
import Register from "./pages/register.tsx";
import Banking from "./pages/banking.tsx";

function App() {
  const [count, setCount] = useState(0)

  return (
<>
    <h1> Test </h1>
    <BrowserRouter>
        <Routes>
            <Route path={"/"} element={<Home />} />
            <Route path={"/about"} element={<About />} />
            <Route path={"/login"} element={<Login />} />
            <Route path={"/register"} element={<Register />} />
            <Route path={"/banking"} element={<Banking />} />
        </Routes>
    </BrowserRouter>
</>
  )
}

export default App
