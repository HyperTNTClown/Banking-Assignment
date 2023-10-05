import Popup from "reactjs-popup";
import styles from "../css/popup.module.css"

interface ResponsePopupProps {
	status: string,
	message: string,
	open: boolean,
	close: () => void
}

export default function ResponsePopup(props: ResponsePopupProps) {
	function cap(string) {
		if (string == null) return ""
		return string.charAt(0).toUpperCase() + string.slice(1);
	}

	console.log(props)
	return (
		<Popup open={props.open} onClose={props.close} modal>
			<div className={styles.popup}>
				<h3>{cap(props.status)}</h3>
				<span>{props.message}</span>
			</div>
		</Popup>
	)
}
