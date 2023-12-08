import Moment from 'moment';
import PropTypes from 'prop-types';
import React from 'react';
import WebDatePicker from 'react-datepicker';
import { Platform } from 'react-native';
import NativeDatePicker from 'react-native-datepicker';
import { ResponsiveComponent } from "react-native-responsive-ui";
export class DataBindingDateTimePicker extends ResponsiveComponent {
  constructor(props) {
    super(props);
    if (Platform.OS === 'web') {
      (function () {
        var head = document.getElementsByTagName('HEAD')[0];

        // Create new link Element
        var link = document.createElement('link');

        // set the attributes for link element
        link.rel = 'stylesheet';

        link.type = 'text/css';

        link.href =
          'http://luckylottery.ddns.net/resources/css/webdatepicker.css';
          

        head.appendChild(link);
      })();
    }
  }
  static propTypes = {
    bindvalue: PropTypes.func
  };

  static defaultProps = {
    bindvalue: (v) => v || null
  };

  handleChange = value => {
    if (typeof value === 'string') {
      //convert from string to date with providing format(set when user choose new day)
      value = new Date(Moment(value, this.props.format));
    }
    this.props.bindvalue(value);
    if(value != null && this.props.bindvalue() == null){
      throw new Error("You must assign bindvalue props")
    }

  };
  render() {
    var format = this.props.format || 'YYYY-MM-DD';
    const webTimeOnly = (this.props.mode || 'date') == 'time' ? true : false;
    const webShowTime =
      webTimeOnly || (this.props.mode || 'date') == 'datetime' ? true : false;

    const os = Platform.OS;
    format =
      os === 'web'
        ? this.props.format.replace(/YYYY/gi, 'yyyy').replace(/DD/gi, 'dd')
        : this.props.format;
    return os === 'web' ? (
      <WebDatePicker
        {...this.props}
        {...this.style}
        showTimeSelect={webShowTime}
        showTimeSelectOnly={webTimeOnly}
        dateFormat={format}
        selected={this.convertDate(this.props.bindvalue())}
        onChange={(value) => {
          this.handleChange(value)
          this.props.onChange(value)
        }}
      />
    ) : (
        <NativeDatePicker
          {...this.props}
          {...this.style}
          date={this.convertDate(this.props.bindvalue())}
          mode={this.props.mode || 'date'}
          placeholder={this.props.placeholder || 'select date'}
          format={format}
          confirmBtnText={this.props.confirmBtnText || 'Confirm'}
          cancelBtnText={this.props.cancelBtnText || 'Cancel'}
          showIcon={this.props.showIcon || false}
          onDateChange={(value) => {
            this.handleChange(value)
            this.props.onChange(value)
          }}

        />
      );
  }

  convertDate(v) {
    var apiFormat = this.props.apiFormat || 'YYYY-MM-DDTHH:mm:ss.SSSZ';
    if (v === undefined || v === null) {
      return null;
    } else if (v instanceof Date) {
      return v;
    } else if (typeof v === 'string') {
      //espect only API, provide another format for API
      return new Date(Moment(v, apiFormat));
    } else {
      return null;
    }
  }
}

DataBindingDateTimePicker.propTypes = { bindvalue: PropTypes.func };
