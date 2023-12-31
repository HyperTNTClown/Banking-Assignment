import './index.css'
import Home from "./pages/home.tsx";
import Banking from "./pages/banking.tsx";
import Register from "./pages/register.tsx";
import Login from "./pages/login.tsx";
import About from "./pages/about.tsx";
import { createBrowserRouter, RouterProvider } from 'react-router-dom'
import {createRoot} from "react-dom/client";
import Verify from "./pages/verify.tsx";
import Overview from "./components/overview.tsx";
import Transfer from "./components/transfer.tsx";
import History from "./components/history.tsx";

const router = createBrowserRouter([
    { path: '/', element: <Home /> },
    { path: '/about', element: <About /> },
    { path: '/login', element: <Login /> },
    { path: '/register', element: <Register /> },
    { path: '/banking', element: <Banking /> ,
			children: [
				{ path: '', element: <Overview /> },
				{ path: 'transfer', element: <Transfer /> },
				{ path: 'history', element: <History /> }
			]
	},
	{ path: '/verify', element: <Verify /> }
], {
    future: {
        v7_normalizeFormMethod: true,
    }
})

createRoot(document.getElementById('root')!).render(
  //<React.StrictMode>
	<RouterProvider router={router} />
  //</React.StrictMode>,
)
