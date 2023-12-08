import PropTypes from 'prop-types';
import React from 'react';
import { Picker } from '@react-native-picker/picker';
import { ResponsiveComponent } from "react-native-responsive-ui";
export class DataBindingPicker extends ResponsiveComponent {
  constructor(props) {
    super(props);
    var items = this.props.items || []
    if(items.length > 0 && this.props.bindvalue() == null){
      var itemValue = this.props.itemValue || 'value'
      this.props.bindvalue(items[0][itemValue])
    }
    this.lastItems = [];
  }
  handleChange = (value, index) => {
    this.props.bindvalue(value);
    if(value != null && this.props.bindvalue() == undefined){
      throw new Error("You must assign bindvalue props")
    }
  };
  static propTypes = {
    bindvalue: PropTypes.func,
  };

  static defaultProps = {
    bindvalue: (v) => v || null,
  };
  renderItem(data,index, itemLabel, itemValue) {
    return <Picker.Item label={data[itemLabel]} value={data[itemValue]} key={index} style={this.props.itemStyles} />;
  }
  async componentDidMount() {

  }
  componentDidUpdate(){
    //check if source changed then switch to 1st item of source
    var items = this.props.items || []
    if(this.lastItems != items){
      if(items.length > 0 && !this.ContainInList(items,this.props.bindvalue())){
        var itemValue = this.props.itemValue || 'value'
        this.props.bindvalue(items[0][itemValue])
      }
    }
    this.lastItems = items;
  }

  ContainInList(items,check){
    var itemValue = this.props.itemValue || 'value'
    return items.filter(a=> a[itemValue] == check).length > 0;
  }

  
  render() {
    var items = this.props.items || []
    var itemLabel = this.props.itemLabel || 'label'
    var itemValue = this.props.itemValue || 'value'
    if(items != null && items.length > 0 && items[0][itemValue] != null && this.props.bindvalue() == undefined){

      //this.props.bindvalue(items[0][itemValue])
    }
    return (

      <Picker
        {...this.props}
        {...this.style}
        onValueChange={this.handleChange}
        selectedValue={this.props.bindvalue()}
        
        style={[this.props.style, { borderColor: 'gray', borderWidth: 1 }]}>
        {items.map((x,index) => {
          return this.renderItem(x,index, itemLabel, itemValue);
        })}
      </Picker>
    );
  }
}