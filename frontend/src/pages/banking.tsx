import useAuth from "../auth.ts";
import {Link, NavLink, Outlet, useLinkClickHandler} from "react-router-dom";
import styles from '../css/banking.module.css'

export default function Banking() {
	const {logout} = useAuth()

	// Handle clicking on a link
	const handleClick = useLinkClickHandler('/')

	return (
		<>
			{/* Navigation bar */}
			<div id={"sidebar"} className={styles.sidebar}>
				<nav>
					<div className={styles.sidebarItem}>
						<img id={"logo"} src={"/icon/icon.svg"}/>
					</div>
					<div className={styles.sidebarItem}>
						{/* @ts-ignore */}
						<NavLink to={'/banking'} end activeClassName={styles.active}>
							<svg className={"svg"} width="24" height="24" viewBox="0 0 24 24"
								 xmlns="http://www.w3.org/2000/svg">
								<path
									d="M 12 2.0996094 L 1 12 L 4 12 L 4 21 L 10 21 L 10 14 L 14 14 L 14 21 L 20 21 L 20 12 L 23 12 L 12 2.0996094 z"></path>
							</svg>
						</NavLink>
					</div>
					<div className={styles.sidebarItem}>
						{/* @ts-ignore */}
						<NavLink to={'/banking/transfer'} activeClassName={styles.active}>
							<svg className={"svg"} width="24" height="24" viewBox="0 0 800 800"
								 xmlns="http://www.w3.org/2000/svg">
								<path
									d="M619.526,200l-109.763,-109.763l47.141,-47.141l190.236,190.237l-190.236,190.237l-47.141,-47.14l109.763,-109.763l-552.859,-0l-0,-66.667l552.859,-0Zm-439.052,333.333l552.859,0l0,66.667l-552.859,0l109.763,109.763l-47.141,47.141l-190.236,-190.237l190.236,-190.237l47.141,47.14l-109.763,109.763Z"/>
							</svg>
						</NavLink>
					</div>
					<div className={styles.sidebarItem}>
						{/* @ts-ignore */}
						<NavLink to={'/banking/history'} activeClassName={styles.active}>
							<svg className={"svg"} width="24" height="24" viewBox="0 0 24 24"
								 xmlns="http://www.w3.org/2000/svg">
								<path d="M12 8v5h5v-2h-3V8z"/>
								<path
									d="M21.292 8.497a8.957 8.957 0 0 0-1.928-2.862 9.004 9.004 0 0 0-4.55-2.452 9.09 9.09 0 0 0-3.626 0 8.965 8.965 0 0 0-4.552 2.453 9.048 9.048 0 0 0-1.928 2.86A8.963 8.963 0 0 0 4 12l.001.025H2L5 16l3-3.975H6.001L6 12a6.957 6.957 0 0 1 1.195-3.913 7.066 7.066 0 0 1 1.891-1.892 7.034 7.034 0 0 1 2.503-1.054 7.003 7.003 0 0 1 8.269 5.445 7.117 7.117 0 0 1 0 2.824 6.936 6.936 0 0 1-1.054 2.503c-.25.371-.537.72-.854 1.036a7.058 7.058 0 0 1-2.225 1.501 6.98 6.98 0 0 1-1.313.408 7.117 7.117 0 0 1-2.823 0 6.957 6.957 0 0 1-2.501-1.053 7.066 7.066 0 0 1-1.037-.855l-1.414 1.414A8.985 8.985 0 0 0 13 21a9.05 9.05 0 0 0 3.503-.707 9.009 9.009 0 0 0 3.959-3.26A8.968 8.968 0 0 0 22 12a8.928 8.928 0 0 0-.708-3.503z"/>
							</svg>
						</NavLink>
					</div>
				</nav>
				<div className={styles.sidebarItem}>
					<Link to={'/'} id={"logout"} onClick={(e) => {
						e.preventDefault()
						logout()
						handleClick(e)

					}}>
						<svg className={"svg"} width="24" height="24" viewBox="0 0 12 12" version="1.1"
							 xmlns="http://www.w3.org/2000/svg">
							<polygon points="9,2 9,0 1,0 1,12 9,12 9,10 8,10 8,11 2,11 2,1 8,1 8,2 "/>
							<polygon
								points="8.2929688,3.2929688 7.5859375,4 9.0859375,5.5 5,5.5 5,6.5 9.0859375,6.5 7.5859375,8   8.2929688,8.7070313 11,6 "/>
						</svg>
					</Link>
				</div>
			</div>
			{/* Main content */}
			<div id={styles.outlet}>
				<Outlet/>
			</div>
		</>
	)
}
