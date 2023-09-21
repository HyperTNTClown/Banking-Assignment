import useAuth from "../auth.ts";
import {Link} from "react-router-dom";
import {useEffect, useRef, useState} from "react";
import {animated, useSpring, to} from "@react-spring/web";
import {useDrag, useGesture} from "@use-gesture/react";
import styles from '../css/register.module.css'

const calcX = (y: number, ly: number) => -(y - ly - window.innerHeight / 2) / 20
const calcY = (x: number, lx: number) => (x - lx - window.innerWidth / 2) / 20

export default function Register() {

	const {register} = useAuth();

	const target = useRef(null)
	const msg = useRef(null)

	const [showForm, setShowForm] = useState(true)

	const [{x, y, rotateX, rotateY, rotateZ, scale, zoom}, api] = useSpring(() => ({
		rotateX: 0,
		rotateY: 0,
		rotateZ: 0,
		scale: 1,
		zoom: 0,
		x: 0,
		y: 0,
		config: {mass: 5, tension: 370, friction: 50}
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
		scatterBackgroundText()
		document.getElementById('firstName')?.focus()
		document.querySelector('body')?.style.setProperty('overflow', 'hidden')
		if (sessionStorage.getItem('username') && sessionStorage.getItem('password')) {
			document.getElementById('email')?.setAttribute('value', sessionStorage.getItem('username')!)
			document.getElementById('password')?.setAttribute('value', sessionStorage.getItem('password')!)
			document.getElementById('password2')?.setAttribute('value', sessionStorage.getItem('password')!)

			sessionStorage.removeItem('username')
			sessionStorage.removeItem('password')
		}
		return () => {
			document.querySelector('body')?.style.setProperty('overflow', 'auto')
		}
	}, []);

	const scatterBackgroundText = () => {
		let background = document.getElementById('background')!!
		background.innerText = ''
		let divs = []
		for (let i = 0; i < 100; i++) {
			let div = document.createElement('div')
			div.innerText = "Register"
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
			<animated.div ref={target} className={styles.registerForm} style={{
				transform: 'perspective(1000px)',
				x,
				y,
				scale: to([scale, zoom], (s, z) => s + z),
				rotateX,
				rotateY,
				rotateZ,
			}}>
				{ showForm ?
				<form onSubmit={(e) => {
					e.preventDefault();
					const firstName = e.currentTarget.firstName.value;
					const lastName = e.currentTarget.lastName.value;
					const email = e.currentTarget.email.value;
					const password = e.currentTarget.password.value;
					const password2 = e.currentTarget.password2.value;
					if (password === password2) {
						setShowForm(false)
						register(email, password, firstName, lastName).then(r => {
							// @ts-ignore
							msg.current.innerText = r.message
						})
					}
				}}>
					<label className={styles.label} htmlFor="firstName">First Name</label>
					<input className={styles.input} type="text" id="firstName" name="firstName"/>
					<label className={styles.label} htmlFor="lastName">Last Name</label>
					<input className={styles.input} type="text" id="lastName" name="lastName"/>
					<label className={styles.label} htmlFor="email">Email</label>
					<input className={styles.input} type="text" id="email" name="email"/>
					<label className={styles.label} htmlFor="password">Password</label>
					<input className={styles.input} type="password" id="password" name="password"/>
					<label className={styles.label} htmlFor="password2">Confirm Password</label>
					<input className={styles.input} type="password" id="password2" name="password2"/>
					<button className={styles.registerButton} type="submit">Register</button>
					<Link to={'/login'} className={styles.loginLink}>Already have an Account?</Link>
				</form>
					: <div className={styles.message}>
						<h1 id={"message"} ref={msg}></h1>
						<Link to={'/login'} className={styles.loginLink}>Login</Link>
					</div>
						}
			</animated.div>
		</>
	)
}
