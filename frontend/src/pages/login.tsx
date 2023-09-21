import {Navigate} from "react-router-dom";
import useAuth from "../auth.ts";
import {useEffect, useRef, useState} from "react";
import styles from '../css/login.module.css'
import {animated, useSpring, to} from "@react-spring/web";
import {useDrag, useGesture} from "@use-gesture/react";

const calcX = (y: number, ly: number) => -(y - ly - window.innerHeight / 2) / 20
const calcY = (x: number, lx: number) => (x - lx - window.innerWidth / 2) / 20

export default function Login() {
	const {login} = useAuth();
	const [success, setSuccess] = useState(false)
	const [registerInstead, setRegisterInstead] = useState(false)

	const target = useRef(null)

	const [{x, y, rotateX, rotateY, rotateZ, scale, zoom}, api] = useSpring(() => ({
		rotateX: 0,
		rotateY: 0,
		rotateZ: 0,
		scale: 1,
		zoom: 0,
		x: 0,
		y: 0,
		config: {mass: 5, tension: 350, friction: 40}
	}))

	useGesture(
		{
			onPinch: ({offset: [d, a]}) => api({zoom: d / 200, rotateZ: a}),
			onMove: ({xy: [px, py], dragging}) =>
				!dragging &&
				api({
					rotateX: calcX(py, y.get()),
					rotateY: calcY(px, x.get()),
					scale: 1.1,
				}),
			onHover: ({hovering}) =>
				!hovering && api({rotateX: 0, rotateY: 0, scale: 1}),
			onMouseUp: () => api({x:0, y:0}),
		},
		{from: () => [0,0], target: target, eventOptions: {passive: false}, }
	)

	useDrag(({active, offset: [x, y]}) =>
		api({x, y, rotateX: 0, rotateY: 0, scale: active ? 1 : 1.1}),
		{from: () => [0,0], target: target, eventOptions: {passive: false}})

	useEffect(() => {
		document.getElementById('username')?.focus()
		document.querySelector('body')?.style.setProperty('overflow', 'hidden')
		scatterBackgroundText()
		return () => {
			document.querySelector('body')?.style.setProperty('overflow', 'auto')
		}
	}, []);

	const redirectRegister = () => {
		// @ts-ignore
		let usrel : HTMLInputElement = document.getElementById('username')!!
		// @ts-ignore
		let pswdel : HTMLInputElement = document.getElementById('password')!!

		sessionStorage.setItem('username', usrel.value)
		sessionStorage.setItem('password', pswdel.value)

		setRegisterInstead(true)
	}

	const scatterBackgroundText = () => {
		let background = document.getElementById('background')!!
		background.innerText = ''
		let divs = []
		for (let i = 0; i < 100; i++) {
			let div = document.createElement('div')
			div.innerText = "Login"
			div.style.position = 'absolute'
			div.style.left = Math.random() * 100 + '%'
			div.style.top = Math.random() * 100 + '%'
			div.style.transform = 'rotate(' + Math.random() * 360 + 'deg)'
			div.style.color = 'rgba(255, 255, 255, ' + Math.random()*.3 + ')'
			div.style.fontSize = Math.random() * 25 + 10 + 'px'
			div.style.fontWeight = 'bold'
			div.style.fontFamily = 'sans-serif'
			div.style.zIndex = '-1'
			div.style.userSelect = 'none'
			div.style.pointerEvents = 'none'
			divs.push(div)
		}
		divs.forEach(div => {
			background.appendChild(div)
		})
	}

	return (
		<>
			<div id={'background'}/>
			<animated.div ref={target} style={{
				transform: 'perspective(600px)',
				x,
				y,
				scale: to([scale, zoom], (s, z) => s + z),
				rotateX,
				rotateY,
				rotateZ,
			}}>
				<form className={styles.loginForm} onSubmit={(e) => {
					e.preventDefault();
					const username = e.currentTarget.username.value;
					const password = e.currentTarget.password.value;
					login(username, password).then(r => {
						if (r.status == "success") {
							setSuccess(true)
						}
					});
				}}>
					<div>
						<label htmlFor="username" className={styles.label}>Username </label>
						<input type="text" id="username" name="username" className={styles.input}/>
					</div>
					<div>
						<label htmlFor="password" className={styles.label}>Password </label>
						<input type="password" id="password" name="password" className={styles.input}/>
					</div>
					{success ? <Navigate to={"/banking"}/> : <button type="submit" className={styles.loginButton}>Login</button>}
					{registerInstead ? <Navigate to={"/register"}/> : <a className={styles.registerLink} onClick={() => redirectRegister()}>Don't have an Account yet?</a>}
				</form>
			</animated.div>
		</>
	)
}
