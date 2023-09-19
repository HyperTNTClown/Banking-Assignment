import App from './app.tsx'
import './index.css'
import './App.css'
import React from "react";
import ReactDOM from "react-dom";
import Home from "./pages/home.tsx";
import Banking from "./pages/banking.tsx";
import Register from "./pages/register.tsx";
import Login from "./pages/login.tsx";
import About from "./pages/about.tsx";
import { createBrowserRouter, RouterProvider } from 'react-router-dom'
import {createRoot} from "react-dom/client";

const router = createBrowserRouter([
    { path: '/', element: <Home /> },
    { path: '/about', element: <About /> },
    { path: '/login', element: <Login /> },
    { path: '/register', element: <Register /> },
    { path: '/banking', element: <Banking /> },
], {
    future: {
        v7_normalizeFormMethod: true,
    }
})

createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <RouterProvider router={router} />
  </React.StrictMode>,
)
