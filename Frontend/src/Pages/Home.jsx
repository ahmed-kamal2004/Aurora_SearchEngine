import RecognitionInterface from "../Components/RecognitionInterface";
import SearchBar from "../Components/SearchBar";
import "../index.css";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
const Home = (props) => {
  const history = useNavigate();
  return (
    <div className="wrapper flex justify-center items-center flex-1 flex-col h-screen text-inherent">
      <img src="src/assets/logo.png" className="block w-[650px] h-[300px]" />
      <SearchBar
        setIsVoice={props.setIsVoice}
        isVoice={props.isVoice}
        Recognition={props.Recognition}
        history={history}
        searchQuery={props.searchQuery}
        setSearchQuery={props.setSearchQuery}
        search={props.search}
        setSearch={props.setSearch}
      />
      {props.isVoice && (
        <RecognitionInterface
          setIsVoice={props.setIsVoice}
          Recognition={props.Recognition}
          history={history}
        />
      )}
    </div>
  );
};
export default Home;
