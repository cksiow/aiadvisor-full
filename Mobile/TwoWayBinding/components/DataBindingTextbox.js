import PropTypes from 'prop-types';
import React from 'react';
import { Platform, TextInput } from 'react-native';
import { ResponsiveComponent } from "react-native-responsive-ui";
export class DataBindingTextbox extends ResponsiveComponent {
  constructor(props) {
    super(props);
    
    this.control = null;
  }
  static propTypes = {
    bindvalue: PropTypes.func,
    inputtype: PropTypes.oneOf(['FreeText', 'Integer', 'Decimal']),
  };

  static defaultProps = {
    bindvalue: (v) => v || '',
    inputtype: 'FreeText',
  };

  handleChange = e => {

      e = e || null;
      if (this.props.bindvalue !== undefined) {
        if (e != null) {
          if (this.props.inputtype != null) {
            if (this.props.inputtype === 'Integer') {
              
              e = /^(-?|-?\d+)$/.test(e) ? parseInt(e) : this.props.bindvalue();
            } else if (this.props.inputtype === 'Decimal') {
              e = /^(-?|-?(\d+\.?\d*|\.\d+))$/.test(e)
                ? parseFloat(e)
                : this.props.bindvalue();
            }
          }
        }
        this.props.bindvalue(e);
        if(e != null && this.props.bindvalue() == null){
          throw new Error("You must assign bindvalue props")
        }
      }
  };
  render() {
    //  {...this.props} = inherits properties
    const { bindvalue, inputtype } = this.props;
    //dont use numeric-pad, it will cause web enter "." not fire events
    const keyboardType = Platform.OS === 'web' ? "default" :  inputtype == 'Integer' || inputtype == 'Decimal' ? 'numeric' : 'default' ;
    return (
      <TextInput
        {...this.props}
        {...this.style}
        ref={control => this.control = control}
        onChangeText={this.handleChange}
        keyboardType={keyboardType}
        value={this.convertString(bindvalue())}
        style={[this.props.style, { borderColor: 'gray', borderWidth: 1 }]}
      />
    );
  }
//this.convertString(bindvalue())
  convertString(v) {
    if (v != null && v != undefined) {
      //resolve android entry cannot accept integer/decimal issue
      return v.toString();
    } else {
      return '';
    }
  }
}
