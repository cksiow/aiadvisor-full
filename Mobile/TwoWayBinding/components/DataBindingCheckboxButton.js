import PropTypes from 'prop-types';
import React from 'react';
import { Text, TouchableOpacity } from 'react-native';
import { ResponsiveComponent } from "react-native-responsive-ui";
export class DataBindingCheckboxButton extends ResponsiveComponent {
    constructor(props) {
        super(props);

    }
    handleChange = (value) => {
        this.props.bindvalue(!this.props.bindvalue());
        if(value != null && this.props.bindvalue() == null){
            throw new Error("You must assign bindvalue props")
          }
    };
    static propTypes = {
        bindvalue: PropTypes.func,
    };

    static defaultProps = {
        bindvalue: (v) => v || null,
    };
    //LIGHTCORAL
    //PALEGREEN
    render() {

        return (
            <TouchableOpacity
                {...this.props}
                {...this.style}
                style={[this.props.style, { backgroundColor: this.props.bindvalue() ? "palegreen" : "lightcoral" }]}
                onPress={() => {

                    this.handleChange(this.props.bindvalue())
                    this.props.onPress(this.props.bindvalue())
                }}>
                <Text style={this.props.textStyle}>
                    {this.props.text}
                </Text>
            </TouchableOpacity>


        );
    }
}