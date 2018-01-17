/* Copyright © 2005-2018 Kuali, Inc. - All Rights Reserved
 * You may use and modify this code under the terms of the Kuali, Inc.
 * Pre-Release License Agreement. You may not distribute it.
 *
 * You should have received a copy of the Kuali, Inc. Pre-Release License
 * Agreement with this file. If not, please write to license@kuali.co.
 */
import React from 'react';
import {assign} from 'lodash';

export class StyledSelect extends React.Component {
	constructor(props) {
		super(props);
	}
	render() {
		let styles = {
			select : {
  				WebkitAppearance: 'button',
				WebkitBorderRadius: 2,
				WebkitBoxShadow: '0px 1px 3px rgba(0, 0, 0, 0.1)',
				WebkitPaddingEnd: '20px',
				WebkitPaddingStart: '2px',
				WebkitUserSelect: 'none',
				backgroundImage: 'url(http://i62.tinypic.com/15xvbd5.png), -webkit-linear-gradient(#FAFAFA, #F4F4F4 40%, #E5E5E5)',
				backgroundPosition: '97% center',
				backgroundRepeat: 'no-repeat',
				border: '1px solid #AAA',
				color: 555,
				fontSize: 'inherit',
				margin: '0px 0 20px 0',
				overflow: 'hidden',
				padding: '5px 10px',
				textOverflow: 'ellipsis',
				whiteSpace: 'nowrap',
				width: 100,
			}
		};

		return (
			<select style={assign(styles.select, this.props.style)} id={this.props.id} name={this.props.name} value={this.props.value} onChange={this.props.onChange}>{this.props.children}</select>
		);
	}
}
