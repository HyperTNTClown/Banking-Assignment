import {Theme} from "react-select";

export const theme: Theme =
	{
		borderRadius: 2,
		spacing: {
			baseUnit: 4,
			controlHeight: 38,
			menuGutter: 2
		},
		colors: {
			primary: '#4d4d4d',
			primary25: '#2d2d2d', // option background
			primary50: '#313131', // option click
			primary75: '#d000ff',
			danger: '#FF1F1F',
			dangerLight: '#FF1F1F',
			neutral0: '#2d2d2d',  // background
			neutral5: '#3fa9ff',
			neutral10: '#651a5f',
			neutral20: '#626262', // unselected border
			neutral30: '#686868', // hover border
			neutral40: '#686868', // arrow hover
			neutral50: 'rgba(239,239,239,0.5)', // placeholder
			neutral60: '#a9a9a9', // selected arrow
			neutral70: '#561d1d',
			neutral80: '#a9a9a9', // arrow selected hover
			neutral90: '#9b1616'
		}
	}
