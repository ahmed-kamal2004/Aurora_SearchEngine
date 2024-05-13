import {redirect} from 'react-router-dom'

const RecognitionInterface = (props) => {
    return (
        <div className="z-10 bg-white shadow-2xl w-[25%] h-[25%] top-1/2 left-1/2 transform -translate-x-1/5 -translate-y-2/3 flex justify-center items-center">
            <div className="text-center">
                <button className="  bg-red-500 rounded-full w-32 h-32 mb-1"><span className="material-symbols-outlined text-center text-white text-[4em]" onClick={ ()=>{
                    props.Recognition.stop();
                    props.setIsVoice(false);
                    setTimeout(() => {
                       props.history('/Results')
                }, 2500); 

                }}>mic</span></button>
                <h2 className="text-3xl mt-2">Speak Now!</h2>
            </div>
        </div>
    )
}

export default RecognitionInterface