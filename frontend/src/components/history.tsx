import useAuth from "../auth.ts";
import {useEffect, useState} from "react";
import {getHistory} from "../bank-api.ts";
import styles from '../css/history.module.css'
import {RingLoader} from "react-spinners";

export default function History() {
	const [history, setHistory] = useState([])
	const [ready, setReady] = useState(false)

	useEffect(() => {
		getHistory([]).then(res => {
			console.log(res)
			setHistory(res.history)
			setReady(true)
		})
	}, []);

	if (!ready) return (<div id={"loading"}><RingLoader/></div>)
	return (
		<div>
			{history.reverse().map((obj, i) => {
				return <div className={styles.card} key={obj.timestamp*obj.amount+obj.from+obj.to}>
					<div className={styles.cardHeader}>
						<div className={styles.cardTitle}>
							{obj.from} {obj.to}
						</div>
						<div className={styles.cardSubtitle}>
							{(new Date(obj.timestamp).toLocaleString())}
						</div>
					</div>
					<div className={styles.cardBody}>
						<div className={styles.cardText}>
							{"$ " + parseFloat(obj.amount)+.0}
						</div>
					</div>
				</div>
			})}
		</div>
	)
}
