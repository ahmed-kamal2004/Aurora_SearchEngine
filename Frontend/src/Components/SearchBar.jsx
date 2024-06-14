import "../index.css"
import { IoIosSearch } from "react-icons/io";
import { HiOutlineMicrophone } from "react-icons/hi";
import { IoCloseOutline } from "react-icons/io5";
import { useRef } from 'react';
const SearchBar = (props) => {
    const searchBarRef = useRef(null);
    const handler = () =>
    {
        props.setSearch(props.search+1);
    }
    return (
        <div className="bg-white w-[40%] relative border rounded-md border-[rgba(0,0,0,0.2)] flex flex-row items-center justify-between px-4 py-2 shadow-2xl">
            <button className="h-16 w-16 text-center"><IoIosSearch className="w-[50%] h-[100%] inline-block " onClick={() => {
                handler();
                if (props.searchQuery != "")
                    props.history(`/Results?q=${props.searchQuery}`)
            }} /></button>
            <input ref={searchBarRef} placeholder="Search anything ..." type="text" name="SearchBar" id="SearchBar" value={props.searchQuery} className="active:border-none w-[75%] h-[100%] px-3 py-1 text-3xl outline-none shadow-md" onChange={(e) => {
                props.setSearchQuery(e.target.value)
            }} />
            <div className="flex justify-around w-[10%]">
                {props.searchQuery && <button className="h-16 w-8" onClick={
                    (e) => {
                        e.preventDefault()
                        searchBarRef.current.value = ""
                        props.setSearchQuery(searchBarRef.current.value)
                    }
                }><IoCloseOutline className="w-[100%] h-[100%] " /></button>}
                <button className="h-16 w-8"><HiOutlineMicrophone className="w-[100%] h-[100%]  " onClick={() => {
                    props.setIsVoice(true);
                    props.Recognition.start();
                    props.Recognition.onresult = (e) => {
                        searchBarRef.current.value = e.results[0][0].transcript
                        props.setSearchQuery(e.results[0][0].transcript)
                    }
                }} /></button>
            </div>
        </div>
    )
}
export default SearchBar;