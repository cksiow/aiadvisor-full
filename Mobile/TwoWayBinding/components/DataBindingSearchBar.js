import PropTypes from 'prop-types';
import React from 'react';
import { Platform } from 'react-native';
import { SearchBar } from 'react-native-elements';
import { ResponsiveComponent } from "react-native-responsive-ui";

export class DataBindingSearchBar extends ResponsiveComponent {
    constructor(props) {
        super(props);
        this.control = null;
    }
    static propTypes = {
        bindvalue: PropTypes.func,
        inputtype: PropTypes.oneOf(['FreeText', 'Integer', 'Decimal', 'RandomInteger']),
        minLength: PropTypes.number,
        maxLength: PropTypes.number,
    };

    static defaultProps = {
        bindvalue: (v) => v || '',
        inputtype: 'FreeText',
        minLength: 0,
        maxLength: 0,


    };

    handleBlur = e => {
        if (this.props.minLength > 0 && this.props.bindvalue() != null && this.props.bindvalue().length < this.props.minLength) this.handleChange('')
    }

    handleChange = e => {
        e = e || null;
        if (this.props.bindvalue !== undefined) {
            if (e != null) {
                if (this.props.maxLength > 0 && e.length > this.props.maxLength) return

                if (this.props.inputtype != null) {
                    if (this.props.inputtype === 'Integer') {
                        e = /^(-?|-?\d+)$/.test(e) ? e : this.props.bindvalue();
                    } else if (this.props.inputtype === 'RandomInteger') {
                        e = /^(-?|-?[0-9?]+)$/.test(e) ? e : this.props.bindvalue();

                    }
                    else if (this.props.inputtype === 'Decimal') {
                        e = /^(-?|-?(\d+\.?\d*|\.\d+))$/.test(e)
                            ? e
                            : this.props.bindvalue();
                    }
                }
            }
        }
        this.props.bindvalue(e);
        if(e != null && this.props.bindvalue() == null){
            throw new Error("You must assign bindvalue props")
          }
    }

    render() {
        const { bindvalue, inputtype } = this.props;
        //dont use numeric-pad, it will cause web enter "." not fire events
        const keyboardType = Platform.OS === 'web' ? "default" : inputtype == 'Integer' || inputtype == 'RandomInteger' || inputtype == 'Decimal' ? 'numeric' : 'default';
        return (
            <SearchBar
                {...this.props}
                {...this.style}
                onChangeText={this.handleChange}
                ref={control => this.control = control}
                onClear={() => this.control.focus()}
                onBlur={this.handleBlur}
                value={this.convertString(bindvalue())}
                keyboardType={keyboardType}
                inputContainerStyle={[this.props.inputContainerStyle, { backgroundColor: "transparent" }]}
                containerStyle={[this.props.containerStyle, { backgroundColor: "transparent", borderWidth: 1 }]}
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
