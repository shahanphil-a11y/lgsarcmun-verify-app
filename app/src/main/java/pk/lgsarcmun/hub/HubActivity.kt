package pk.lgsarcmun.hub

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class HubActivity : ComponentActivity() {
 private val cameraPermission=registerForActivityResult(ActivityResultContracts.RequestPermission()){ }
 override fun onCreate(state:Bundle?){super.onCreate(state);setContent{HubApp{cameraPermission.launch(Manifest.permission.CAMERA)}}}
}
@Composable fun HubApp(requestCamera:()->Unit){var page by remember{mutableStateOf("home")};MaterialTheme(colorScheme=darkColorScheme(primary=Color(0xFF60A5FA),background=Color(0xFF030712),surface=Color(0xFF101827))){Scaffold(containerColor=Color(0xFF030712),bottomBar={NavigationBar(containerColor=Color(0xCC07111F)){listOf("home" to "Home","scan" to "Scan","member" to "Membership","more" to "More").forEach{(id,label)->NavigationBarItem(selected=page==id,onClick={page=id},icon={Text(label.first().toString(),fontWeight=FontWeight.Bold)},label={Text(label)})}}}){p->Box(Modifier.padding(p).fillMaxSize()){when(page){"home"->HomePage{page="scan"};"scan"->ScanPage(requestCamera);"member"->MembershipPage{page="auth"};"auth"->AuthPage{page="member"};else->MorePage()}}}}}
@Composable fun BrandHeader(){Column(Modifier.padding(24.dp)){Text("LGSARCMUN",color=Color.White,fontSize=13.sp,fontWeight=FontWeight.Bold,letterSpacing=3.sp);Text("Hub",color=Color(0xFF60A5FA),fontSize=40.sp,fontWeight=FontWeight.Black);Text("Official membership platform",color=Color(0xFF94A3B8),fontSize=14.sp)}}
@Composable fun GlassCard(modifier:Modifier=Modifier,content:@Composable ColumnScope.()->Unit){Card(modifier,shape=RoundedCornerShape(28.dp),colors=CardDefaults.cardColors(containerColor=Color(0x0FFFFFFF)),border=androidx.compose.foundation.BorderStroke(1.dp,Color(0x22FFFFFF))){Column(Modifier.padding(22.dp),content=content)}}
@Composable fun HomePage(openScan:()->Unit){Column(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF0B1D33),Color(0xFF030712))))){BrandHeader();GlassCard(Modifier.padding(20.dp).fillMaxWidth()){Text("Everything LGSARCMUN, in one place.",color=Color.White,fontSize=24.sp,fontWeight=FontWeight.Bold);Text("Membership, identity and secure QR scanning.",color=Color(0xFFCBD5E1),modifier=Modifier.padding(top=8.dp));Button(onClick=openScan,modifier=Modifier.fillMaxWidth().padding(top=20.dp),shape=RoundedCornerShape(16.dp)){Text("SCAN MEMBERSHIP")}};Text("Quick access",color=Color.White,fontSize=18.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(22.dp,22.dp,22.dp,12.dp));Row(Modifier.padding(horizontal=20.dp),horizontalArrangement=Arrangement.spacedBy(12.dp)){QuickTile("Membership","Digital ID",Modifier.weight(1f));QuickTile("Announcements","Stay updated",Modifier.weight(1f))}}}
@Composable fun QuickTile(title:String,subtitle:String,modifier:Modifier){GlassCard(modifier){Text(title,color=Color.White,fontWeight=FontWeight.Bold);Text(subtitle,color=Color(0xFF94A3B8),fontSize=12.sp,modifier=Modifier.padding(top=5.dp))}}
@Composable fun ScanPage(requestCamera:()->Unit){val context=LocalContext.current;val granted=ContextCompat.checkSelfPermission(context,Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED;Column(Modifier.fillMaxSize().background(Color(0xFF030712)),horizontalAlignment=Alignment.CenterHorizontally){Text("QR Scanner",color=Color.White,fontSize=30.sp,fontWeight=FontWeight.Black,modifier=Modifier.padding(top=25.dp));Text("Scan a membership QR code",color=Color(0xFF94A3B8));if(granted){QrScanner(Modifier.padding(20.dp).fillMaxWidth().aspectRatio(1f))}else{Spacer(Modifier.height(60.dp));Button(onClick=requestCamera){Text("ENABLE CAMERA")}}}}
@Composable fun QrScanner(modifier:Modifier){val context=LocalContext.current;AndroidView(modifier=modifier, factory={PreviewView(it).apply{scaleType=PreviewView.ScaleType.FILL_CENTER}}, update={view->val providerFuture=ProcessCameraProvider.getInstance(context);providerFuture.addListener({val provider=providerFuture.get();val preview=Preview.Builder().build().also{it.surfaceProvider=view.surfaceProvider};val scanner=BarcodeScanning.getClient();val analysis=ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();analysis.setAnalyzer(ContextCompat.getMainExecutor(context)){proxy->val media=proxy.image;if(media==null){proxy.close();return@setAnalyzer};scanner.process(InputImage.fromMediaImage(media,proxy.imageInfo.rotationDegrees)).addOnSuccessListener{codes->if(codes.any{it.rawValue?.isNotBlank()==true}){/* QR detected; membership verification endpoint can consume rawValue */}}.addOnCompleteListener{proxy.close()}};try{provider.unbindAll();provider.bindToLifecycle(context as androidx.lifecycle.LifecycleOwner,CameraSelector.DEFAULT_BACK_CAMERA,preview,analysis)}catch(_:Exception){}},ContextCompat.getMainExecutor(context))})}
@Composable fun MembershipPage(openAuth:()->Unit){Column(Modifier.fillMaxSize().background(Color(0xFF030712))){Text("Membership",color=Color.White,fontSize=30.sp,fontWeight=FontWeight.Black,modifier=Modifier.padding(24.dp));GlassCard(Modifier.padding(20.dp).fillMaxWidth()){Text("YOUR DIGITAL ID",color=Color(0xFF60A5FA),fontSize=11.sp,fontWeight=FontWeight.Bold);Text("Sign in to view your membership card.",color=Color.White,fontSize=19.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(top=10.dp));Button(onClick=openAuth,modifier=Modifier.fillMaxWidth().padding(top=20.dp)){Text("SIGN IN / SIGN UP")}}}}
@Composable fun AuthPage(done:()->Unit){Column(Modifier.fillMaxSize().background(Color(0xFF030712)).padding(24.dp),verticalArrangement=Arrangement.Center){BrandHeader();GlassCard(Modifier.fillMaxWidth()){Text("Member account",color=Color.White,fontSize=24.sp,fontWeight=FontWeight.Bold);Text("Sign in or create your LGSARCMUN membership account.",color=Color(0xFF94A3B8),modifier=Modifier.padding(top=8.dp));Button(onClick=done,modifier=Modifier.fillMaxWidth().padding(top=20.dp)){Text("CONTINUE")}}}}
@Composable fun MorePage(){Column(Modifier.fillMaxSize().background(Color(0xFF030712))){Text("More",color=Color.White,fontSize=30.sp,fontWeight=FontWeight.Black,modifier=Modifier.padding(24.dp));listOf("Announcements","Settings","Help & Support","About LGSARCMUN Hub").forEach{Text(it,color=Color.White,fontSize=17.sp,modifier=Modifier.fillMaxWidth().padding(20.dp))}}}
